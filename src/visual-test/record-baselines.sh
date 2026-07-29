#!/usr/bin/env bash
#
# Records the image baselines, or compares against them with --check.
#
# Text rasterises differently on every platform, so both happen in one fixed place: the Playwright image
# pinned to the same version as the dependency, on the architecture the CI runner uses. Running the test on
# the host would compare the host's font stack against the container's and fail for no reason.

set -euo pipefail

IMAGE="mcr.microsoft.com/playwright/java:v1.61.0"
# The runner is amd64; on an arm host the image runs emulated so that the pictures still match
PLATFORM="linux/amd64"

REPOSITORY="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

ARGUMENTS=(-Dtest=VisualBaselineTest -DfailIfNoSpecifiedTests=false)
if [[ "${1:-}" == "--check" ]]; then
  echo "Comparing the rendering against the recorded baselines..."
else
  ARGUMENTS+=(-Dvisual.baseline.update=true)
  echo "Recording the baselines. Review the diff before committing them."
fi

# Compiled on the host: the image ships JDK 25, which the Lombok version this project pins does not support.
# Only the browser has to be the container's, and it runs bytecode built for 21 without complaining.
echo "Compiling on the host..."
mvn --batch-mode -q -P visual-parity test-compile

docker run --rm \
  --platform "${PLATFORM}" \
  --volume "${REPOSITORY}:/repository" \
  --volume "${HOME}/.m2:/root/.m2" \
  --workdir /repository \
  --env VISUAL_BASELINE_CONTAINER=1 \
  "${IMAGE}" \
  mvn --batch-mode -P visual-parity surefire:test "${ARGUMENTS[@]}"
