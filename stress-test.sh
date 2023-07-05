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
compiledir="build"
testcnt=10
trash="trash"

function search_good_compile_dir() {
  i=0
  while [ -d $compiledir$i ]; do
    i=$(($i + 1))
  done
  compiledir=$compiledir$i
  mkdir $compiledir
}

function print_usage() {
  echo "$1"
  echo "Usage: <input-config> <good-solution> <bad-solution> [<tests-in-group>]"
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
    javac $1 -d $compiledir
  elif [[ $1 =~ ".cpp" ]]; then
    g++ -o $compiledir/$1.out $1
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
    ./$1.out
  elif [[ $1 =~ ".kt" ]]; then
    classname=${1::-3}
    kotlin $$classname
  else
    echo "Расширение не поддерживается"
  fi < $2 > $3
}

# fix?
function get_truth_name() {
  echo $1 | rev | cut -d '/' -f 1 | rev
}

function finish() {
  {
    cd ..
    rm -f $out1 $out2 $manifest $jarfile && removedir $testdir && removedir $compiledir && echo "[+] Files deleted!"
  } || {
    echo "Error while deleting files"
    exit 84
  }
}

function error() {
  echo $1
  finish
  exit $2
}


if [[ $# -ne 3 ]] && [[ $# -ne 4 ]]; then
  print_usage "Illegal count of arguments"
elif [[ $# -eq 4 ]]; then
  testcnt=$4
fi

search_good_compile_dir
check_existing $1 Config
check_existing $2 Good-solution
check_existing $3 Bad-solution

echo "Manifest-Version: 1.0" > $manifest
echo "Main-Class: parser.ConfigureParser" >> $manifest

# make jar
{
  javac -d OUT $(find src -name \*.java) &&
    jar --create --manifest $manifest --file $compiledir/$jarfile -C OUT . &&
    removedir OUT && echo "[+] Jar-file created!"
} || {
  error 115 "Error while making jar-file"
}

# compile
{
  compile $2 && compile $3 && echo "[+] Build completed!"
  touch $compiledir/$trash
} || {
  error 123 "Error while compiling"
}

# run tests
cd $compiledir # || echo "directory not found" && exit 121
fl="1"
for (( c = 1; c <= 10; c+=1 )) do
  {
    java -jar stress-test.jar ../$1 $testcnt $testdir
  } || {
    error 134 "Error while generating tests"
  }
  for (( i = 1; i <= $testcnt; i++ )); do
    {
      run $2 $testdir/test$i $out1 > $trash
    } || {
      continue
    }
    {
      run $3 $testdir/test$i $out2 > $trash
    } || {
      echo "[-] Test $(( ($c - 1) * $testcnt + $i )) failed!"
      echo "Testcase - in file \"$failed\""
      cp $testdir/test$i ../$failed
      echo Expected: $(cat $out1), found: exception
      fl="0"
      break
    }
    if [[ $(diff $out1 $out2) ]]; then
      echo "[-] Test $(( ($c - 1) * $testcnt + $i )) failed!"
      echo "Testcase - in file \"$failed\""
      cp $testdir/test$i ../$failed
      echo Expected: $(cat $out1), found: $(cat $out2)
      fl="0"
      break
    fi
  done
  if [[ $fl == "0" ]]; then break; fi
  echo "[+]" $(($testcnt * $c)) "tests passed!"
done

# cleaning
finish
