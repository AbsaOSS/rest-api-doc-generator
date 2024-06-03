#!/bin/bash
# ------------------------------------------------------------------------
# Copyright 2020 ABSA Group Limited
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------
#
# THIS SCRIPT IS INTENDED FOR LOCAL DEV USAGE ONLY
#

SCALA_VERSIONS=(2.11 2.12 2.13)

BASE_DIR=$(dirname "$0")
MODULE_DIRS=$(find "$BASE_DIR" -type f -name "pom.xml" -printf '%h\n')
MVN_EXEC="mvn"

print_title() {
  echo "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░"
  echo "                           $1                                                  "
  echo "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░"
}

cross_build() {
  bin_ver=$1
  command=$2
  shift 2
  additional_options="$*"

  # pre-cleaning
  for dir in $MODULE_DIRS; do
    rm -rf "$dir"/target
  done

  print_title "Switching to Scala $bin_ver"
  $MVN_EXEC scala-cross-build:change-version -Pscala-"$bin_ver"

  print_title "Building with Scala $bin_ver"
  $MVN_EXEC $command -Pscala-"$bin_ver" $additional_options || exit 1
}

# -------------------------------------------------------------------------------

if [ $# -eq 0 ]; then
  echo "Usage: ./build-all.sh <command> [options]"
  echo "Commands:"
  echo "  install: Run the install command"
  echo "  deploy: Run the deploy command"
  echo "Options:"
  echo "  Any additional options that should be passed to the Maven command"
  exit 1
fi

cmd_arg=$1
shift
additional_options="$*"

case "$cmd_arg" in
  "deploy")
    command="deploy -Ddeploy -Dossrh"
    ;;
  "install")
    command="install"
    ;;
  *)
    echo "Error: Command '${cmd_arg}' not recognized."
    exit 1
    ;;
esac

for v in "${SCALA_VERSIONS[@]}"; do
  cross_build "$v" "$command" "$additional_options"
done

print_title "Restoring POM-files"
$MVN_EXEC scala-cross-build:change-version -Pscala-"${SCALA_VERSIONS[0]}"

# remove backup files
for dir in $MODULE_DIRS; do
  rm -f "$dir"/pom.xml.bkp
done
