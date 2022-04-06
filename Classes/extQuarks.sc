+ Quarks{
    *findClassPackage{|class|
        var result;

        Quarks.installed.do{|quark|
            var classes = Quarks.classesInPackage(quark.name.asSymbol);
            if(classes.indexOfEqual(class).notNil, { result = quark });
        };

        ^result
    }
}
