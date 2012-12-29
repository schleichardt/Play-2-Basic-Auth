xsbt="$(pwd)/sbt"
cd Plugin && $xsbt -Dsbt.log.noformat=true clean test publish-local && cd ../TestApp && $xsbt -Dsbt.log.noformat=true clean test