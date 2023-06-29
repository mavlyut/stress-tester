#!/bin/bash
# shellcheck disable=SC2004
# shellcheck disable=SC2046
# shellcheck disable=SC2076
# shellcheck disable=SC2086
# shellcheck disable=SC2154
# shellcheck disable=SC2164

out1=".out1"
out2=".out2"
testdir="tests"
failed="failed_test_input"
jarfile="stress-test.jar"
manifest="MANIFEST.MF"
testcnt=10

function print_usage() {
  echo "$1"
  echo "Usage: <input-config> <good-solution> <bad-solution>"
  exit 20
}

function removedir() {
  if [[ -d $1 ]] && [[ $1 != "/" ]]; then
    rm -r $1
  fi
}

function check_existing() {
  if [[ ! -f $1 ]]; then
    print_usage "$2 file not found"
    exit 32
  fi
}

function compile() {
  if [[ $1 =~ ".java" ]]; then
    javac $1
  elif [[ $1 =~ ".cpp" ]]; then
    g++ -o moscow.out $1
  elif [[ $1 =~ ".kt" ]]; then
    kotlinc $1
  else
    echo "Расширение не поддерживается"
  fi
}

function run() {
  if [[ $1 =~ ".java" ]]; then
    classname=${1::-5}
    java $classname
  elif [[ $1 =~ ".cpp" ]]; then
    ./moscow.out
  elif [[ $1 =~ ".kt" ]]; then
    classname=${1::-3}
    kotlin $$classname
  else
    echo "Расширение не поддерживается"
  fi < $2 > $3
}

function clean() {
  if [[ $1 =~ ".java" ]]; then
    classname=${1::-5}
    rm $classname.class
  elif [[ $1 =~ ".cpp" ]]; then
    rm ./moscow.out
  elif [[ $1 =~ ".kt" ]]; then
     classname=${1::-3}
     rm $classname.class
  else
    echo "Расширение не поддерживается"
  fi
}


if [[ $# -ne 3 ]]; then
  print_usage "Count of args must be equals 3"
fi

check_existing $1 Config
check_existing $2 Good-solution
check_existing $3 Bad-solution

echo "Manifest-Version: 1.0" > $manifest
echo "Main-Class: parser.ConfigureParser" >> $manifest

# make jar
{
  javac -d OUT $(find . -name \*.java) &&
    jar --create --manifest $manifest --file $jarfile -C OUT . &&
    removedir OUT
} || {
  echo "Error while making jar-file"
  exit 95
}

# compile
{
  compile $2 && compile $3 && echo "Build completed!"
} || {
  echo "Error while compiling"
  exit 103
}

# run tests
fl="1"
for (( c = 1; c <= 10; c+=1 )) do
  java -jar stress-test.jar $1 $testcnt $testdir
  for (( i = 1; i <= $((testcnt)); i++ )); do
    {
      run $2 $testdir/test$i $out1
    } || {
      i=$(($i - 1)) # todo?
      continue
    }
    {
      run $3 $testdir/test$i $out2
    } || {
      echo "Test $(( ($c - 1) * $testcnt + $i )) failed!"
      echo "Testcase - in file \"$failed\""
      cp $testdir/test$i $failed
      echo Expected: $(cat $out1), found: exception
      fl="0"
      break
    }
    if [[ $(diff $out1 $out2) ]]; then
      echo "Test $(( ($c - 1) * $testcnt + $i )) failed!"
      echo "Testcase - in file \"$failed\""
      cp $testdir/test$i $failed
      echo Expected: $(cat $out1), found: $(cat $out2)
      fl="0"
      break
    fi
  done
  if [[ $fl == "0" ]]; then break; fi
  echo $(($testcnt * $c)) tests passed!
done

# cleaning
{
  rm -f $out1 $out2 $manifest $jarfile && removedir $testdir && clean $2 && clean $3
} || {
  echo "Error while deleting files"
  exit 134
}
