xsbt="$(pwd)/sbt"
cd Plugin && $xsbt -Dsbt.log.noformat=true clean test publish-local && cd ../ScalaTestApp && $xsbt -Dsbt.log.noformat=true clean test