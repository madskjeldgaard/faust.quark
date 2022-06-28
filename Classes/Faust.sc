/*

An interface for both the faust command and faust2sc.py command

*/
Faust {
    classvar <>flags = "-double -vec";
    classvar <>commandPathPrefix="";
    classvar <>supportedVersion="2.40.0";
    classvar <>headerPath;

    *faustFileExtensions{
        ^["dsp"]
    }

    *version{
        var versionOut = commandPathPrefix ++ "faust --version".unixCmdGetStdOut;

        // This regex searches for a version in the style 1.1.1
        ^versionOut.findRegexp("[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+").first[1]
    }

    *checkVersion{|largerThanOrEqual="2.40.0"|
        ^this.version >= largerThanOrEqual
    }

    // Checks pathname extension to see if it is valid
    *isFaustFile{|file|
        var fileName = this.prAsPathName(file);
        ^this.faustFileExtensions.any{|ext| ext == fileName.extension }
    }

    *parse{|faustFile|
        var result = "%faust -i \"%\" -json > /dev/null".format(
            commandPathPrefix,
            faustFile,
        ).postln.unixCmdGetStdOut();
        var dict = "%.json".format(faustFile).parseJSONFile();
        File.delete("%.json");
        ^dict
    }

    // Make sure a file is a pathname object
    *prAsPathName{|file|
        ^case
        { file.class == String }   {
            PathName(file)
        }
        { file.class == PathName } {
            file
        }
        { file.class != String and: { file.class != PathName } } { "file must be a string or a pathname".error };
    }
}

Faust2SC : Faust{
    // Compiles and installs
    *compileFile{|file, outputDir|
        var fileFolder, cmd, result;

        file = this.prAsPathName(file);
        fileFolder = file.pathOnly;

        "%: Compiling file %".format(this.name, file.fileName).postln;

        // FIXME: cd is necessary at the moment because the command spits out some junk and if we don't cd it is going to leave it all over your computer.
        cmd = "cd \"%\"; % \"%\" -s -o \"%\" %".format(
            fileFolder,
            this.getCompilerCommand(),
            file.fileName,
            outputDir.fullPath,
            flags
        );

        if(Faust.headerPath.notNil, {
            cmd = cmd ++ " -p \"%\"".format(Faust.headerPath);
        });

        result = cmd.systemCmd;

        if(result == 0, {
            "%: Successfully compiled % to %".format(this.name, file.fileName, outputDir.folderName).postln
        }, {
            "%: Could not compile %".format(this.name, file.fileName).warn
        });

        ^result
    }

    // Compiles all faust files in directory
    *compileAllFilesInDir{|inputDir, outputDir|
        var compiled = [];
        var compiledOK;
        inputDir = this.prAsPathName(inputDir);
        inputDir.files.do{|file|
            // Skip compilation if an error happened earlier
            if(compiled.every{|isTrue| isTrue}, {
                if(this.isFaustFile(file), {
                    var result = this.compileFile(file, outputDir);

                    compiled = compiled.add(result == 0);
                })
            })
        };

        compiledOK = compiled.every{|didCompile| didCompile };

        if(compiledOK, {
            "%: Successfully compiled faust files in %".format(this.name, outputDir.folderName).postln;

        }, {
            "%: Failed compiling faust files in %".format(this.name, outputDir.folderName).error;
        })

    }

    *getCompilerCommand{
        ^if(this.checkVersion(supportedVersion), {
            "%faust2sc.py".format(commandPathPrefix)
        }, {
            "Faust version % not supported. Please upgrade to %".format(this.version(), supportedVersion).error.throw;
        })

    }

    *compilerExists{
        var checkCmd = "command -v ".format(this.getCompilerCommand());
        ^checkCmd.systemCmd == 0
    }

}
