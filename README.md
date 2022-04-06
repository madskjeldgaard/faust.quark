# Faust.quark

This SuperCollider package makes it possible to create SuperCollider packages (Quarks) containing plugins written in Faust code. With this, you can distribute plugins written in Faust and make it easy for others to install, compile or uninstall them. It also contains some simple interfaces for the `faust` and `faust2sc.py` commands used behind the scenes.

## Prerequisites

This package requires **faust version 2.40.0 or higher** to work.

## How to make a Faust quark with this package

If you've never made a SuperCollider package before, read [this guide](https://doc.sccode.org/Guides/UsingQuarks.html) and/or try generating one using [this cookiecutter recipe](https://github.com/madskjeldgaard/cookiecutter-quark).

If you want to see a finished package that uses this method, see [this example Quark](https://github.com/madskjeldgaard/faustquark-example).

### Step 0: Add faust files to your project

This package assumes that you have `.dsp` faust files in your project in a folder at the root of your project called `faust` (if you want to change this, see the help files). Every `.dsp` file in this folder will be part of your installation.

### Step 1: Add dependency

In your Quark folder, open up the *.quark* file and add this package as a dependency eg:

```supercollider
(
  name: "mycoolfaustplugins",
  summary: "Only the world's coolest plugins",
  version: "0.001",
  schelp: "MyCoolFaust.schelp",
  dependencies: ["https://github.com/madskjeldgaard/faust.quark"], // <----- Add Faust.quark here
  license: "GNU GPL v3.0",
  copyright: "yourname",
  ext_dependency: nil,
  url: "https://github.com/yourname/mycoolfaustplugins"
)
```
With this done, when people install your quark it will automatically download this package along with it.

### Step 2: Create a subclass

In your Quark, create a simple class that inherits from the `AbstractFaustPackage` class defined in this package:

```supercollider
MyCoolFaust : AbstractFaustPackage {}
```

From now on, your users can install your plugins like this:
```supercollider
MyCoolFaust.install();
```
Or uninstall like this:
```supercollider
MyCoolFaust.uninstall();
```

### Step 3 (optional): Automatically compile and install

If you want to automatically compile and install the plugins, you can do the following in your subclass:

```supercollider
MyCoolFaust : AbstractFaustPackage{
    *initClass{
        this.autoCompileAtStartup()
    }
}
```

With this, after the first class library compilation, Faust.quark will check if the plugins are installed and if not it will install them automatically.

That's it. See the help files for more information.

## Installation

Open up SuperCollider and evaluate the following line of code:
`Quarks.install("https://github.com/madskjeldgaard/faust.quark")`
