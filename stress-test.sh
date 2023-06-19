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
  exit 12
}

function removedir() {
  if [[ -d $1 ]] && [[ $1 != "/" ]]; then
    rm -r $1
  fi
}

function check_existing() {
  if [[ ! -f $1 ]]; then
    print_usage "$2 file not found"
  fi
}

function compile() {
  if [[ $1 =~ ".java" ]]; then
    classname=${1::-5}
    javac $1
  elif [[ $1 =~ ".cpp" ]]; then
    g++ -o moscow.out $1
  else
    echo "Расширение не поддерживается"
  fi
}

function run() {
  if [[ $1 =~ ".java" ]]; then
    classname=${1::-5}
    java $classname < $2 > $3
  elif [[ $1 =~ ".cpp" ]]; then
    ./moscow.out < $2 > $3
  else
    echo "Расширение не поддерживается"
  fi
}

function clean() {
  if [[ $1 =~ ".java" ]]; then
    classname=${1::-5}
    rm $classname.class
  elif [[ $1 =~ ".cpp" ]]; then
    rm ./moscow.out
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

javac -d OUT $(find . -name \*.java)
jar --create --manifest $manifest --file $jarfile -C OUT .
removedir OUT

compile $2
compile $3
echo Build completed!

fl=true
for (( c = 1; c <= 10; c+=1 )) do
  java -jar stress-test.jar $1 $testcnt $testdir
  for (( i = 1; i <= $((testcnt)); i++ )); do
    run $2 $testdir/test$i $out1
    run $3 $testdir/test$i $out2
    if [[ $(diff $out1 $out2) ]]; then
      echo Test failed!
      echo Testcase - in file \"$failed\"
      cp $testdir/test$i $failed
      echo Expected: $(cat $out1), found: $(cat $out2)
      fl=false
      break
    fi
  done
  if [[ ! $fl ]]; then break; fi
  echo $(($testcnt*$c)) tests passed!
done

rm $out1 $out2 $manifest $jarfile
removedir $testdir
clean $2
clean $3
