[ Ice ].as(function(This) {
    This.Cookie = This.Parameter.Association.subclass({
        initialize: function(name, value) {
            this.name = name;
            this.value = value || '';
            this.save();
        },

        saveValue: function(value) {
            this.value = value;
            this.save();
        },

        loadValue: function() {
            this.load();
            return this.value;
        },

        save: function() {
            document.cookie = this.name + '=' + this.value;
            return this;
        },

        load: function() {
            var foundTuple = This.Cookie.parse().detect(function(tuple) {
                return this.name == tuple[0];
            }.bind(this));
            this.value = foundTuple[1];
            return this;
        },

        remove: function() {
            document.cookie = this.name + '=0; expires=' + (new Date).toGMTString();
        }
    });

    This.Cookie.all = function() {
        return This.Cookie.parse().collect(function(tuple) {
            var name = tuple[0];
            var value = tuple[1];
            return new This.Cookie(name, value);
        });
    };

    This.Cookie.lookup = function(name) {
        var foundTuple = This.Cookie.parse().detect(function(tuple) {
            return name == tuple[0];
        });
        if (foundTuple) {
            return new This.Cookie(foundTuple[0], foundTuple[1]);
        } else {
            throw 'Cannot find cookie named: ' + name;
        }
    };

    //private
    This.Cookie.parse = function() {
        return document.cookie.split('; ').collect(function(tupleDetails) {
            return tupleDetails.contains('=') ? tupleDetails.split('=') : [tupleDetails, ''];
        });
    };
});