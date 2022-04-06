/*

A superclass for Quarks that distribute faust files. Use this to make your faust package easily installable.

*/

AbstractFaustPackage {
    // This assumes you have a folder called "faust" at the root of your quark
    classvar <faustFilesFolder = "faust";
    classvar package;

    *thisPackage{
        package = Quarks.findClassPackage(this)
        ^package
    }

    *thisPackageName{
        ^this.thisPackage().name
    }

    *compiledFilesFolder{
        ^Platform.userExtensionDir.asPathName +/+ (this.thisPackageName() ++ "FaustPlugins")
    }

    *folderExists{
        ^this.compiledFilesFolder().isFolder
    }

    *makeFolder{
        File.mkdir(this.compiledFilesFolder().fullPath);
    }

    *install{
        var outputDir = this.compiledFilesFolder();
        var inputDir = this.thisPackage().localPath +/+ faustFilesFolder;

        if(outputDir.isFolder.not, {
            this.makeFolder();
        });

        if(outputDir.isFolder.not, {
            "%: % is not a folder".format(this.name, outputDir).warn
        });

        if(inputDir.isFolder.not, {
            "%: % is not a folder".format(this.name, inputDir).warn
        });

        if(inputDir.isFolder && outputDir.isFolder, {
            Faust2SC.compileAllFilesInDir(inputDir, outputDir)
        })
    }

    *uninstall{
        var installationFolder = this.compiledFilesFolder();
        if(installationFolder.isFolder.not, {
            "%: Cannot uninstall % - it does not exist".format(this.name, installationFolder.fullPath).error
        }, {
            "%: UNINSTALLING %".format(this.name, installationFolder.fullPath).postln;
            File.deleteAll(installationFolder.fullPath)
        })
    }

    // Automatically compile and install if not already installed and not the superclass
    *autoCompileAtStartup{
        // autocompileAdded.not.if{
            StartUp.add({
                if(this.folderExists.not, {
                    "%: Automatically compiling Faust plugins\n".format(this.name).postln;
                    this.install();
                }, {
                    "%: Not compiling Faust plugins. Seemingly it's already installed at %".format(this.name, this.compiledFilesFolder()).postln;
                })
            });

        //     autocompileAdded = true;
        //
        // }
    }

}
