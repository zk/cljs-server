if(!Function.prototype.bind){Function.prototype.bind = function(scope) {var _function = this;return function() { return _function.apply(scope, arguments); } }}var cljs = cljs || {};
cljs.core = cljs.core || {};
(function() {

  this.Array = Array;
  
  for(var prop in cljs.core){ this[prop] = cljs.core[prop] };
  
  this._ = _;
  
  this.count = (function(col){
    return (function(){
      if(col){
       return col.length;
      } else {
       return 0;
      }
    }.bind(this))();
  }.bind(this));
  
  this.first = (function(col){
    return (function(){
      
      if(!col) return null;
      
      return (col[0]);
    
    }.bind(this))();
  }.bind(this));
  
  this.second = (function(col){
    return this.nth(col, 1);
  }.bind(this));
  
  this.rest = (function(col){
    return (function(){
      
      if(!col) return null;
      
      return this.Array.prototype.slice["call"](col,1);
    
    }.bind(this))();
  }.bind(this));
  
  this.inc = (function(n){
    return (function() {
      var out = arguments[0];
      for(var __i=1; __i<arguments.length; __i++) {
        out = out + arguments[__i];
      }
      return out;
    }).call(this, n, 1);
  }.bind(this));
  
  this.dec = (function(n){
    return (function() {
      var out = arguments[0];
      for(var __i=1; __i<arguments.length; __i++) {
        out = out - arguments[__i];
      }
      return out;
    }).call(this, n, 1);
  }.bind(this));
  
  this.nth = (function(col, n){
    return (function(){
      
      if(!(col && (col.length > n))) return null;
      
      return (col[n]);
    
    }.bind(this))();
  }.bind(this));
  
  this.last = (function(col){
    return (col[this.dec(col.length)]);
  }.bind(this));
  
  this.reduce = (function(f, initial, col){
    return (function(){
      var i = (function(){
        if(col){
         return initial;
        } else {
         return null;
        }
      }.bind(this))(),
      c = (function(){
        if(col){
         return col;
        } else {
         return initial;
        }
      }.bind(this))();
      
      return (function(){
        if(i){
         return this._["reduce"](c,f,i);
        } else {
         return this._["reduce"](c,f);
        }
      }.bind(this))();
    
    }.bind(this))();
  }.bind(this));
  
  this.map = (function(f, initial, col){
    return (function(){
      var i = (function(){
        if(col){
         return initial;
        } else {
         return null;
        }
      }.bind(this))(),
      c = (function(){
        if(col){
         return col;
        } else {
         return initial;
        }
      }.bind(this))();
      
      return (function(){
        
        if(!c) return null;
        
        return (function(){
          if(i){
           return this._["map"](c,f,i);
          } else {
           return this._["map"](c,f);
          }
        }.bind(this))();
      
      }.bind(this))();
    
    }.bind(this))();
  }.bind(this));
  
  this.str = (function(){
    var args = Array.prototype.slice.call(arguments, 0);
    return this.reduce((function(col, el){
      return (function() {
        var out = arguments[0];
        for(var __i=1; __i<arguments.length; __i++) {
          out = out + arguments[__i];
        }
        return out;
      }).call(this, col, el);
    }.bind(this)), "", this.filter((function(p1__6225_HASH_){
      return this._["identity"](p1__6225_HASH_);
    }.bind(this)), args));
  }.bind(this));
  
  this.println = (function(){
    var args = Array.prototype.slice.call(arguments, 0);
    return console["log"](args);
  }.bind(this));
  
  this.apply = (function(f, args){
    return f["apply"](this,args);
  }.bind(this));
  
  this.filter = (function(f, col){
    return (function(){
      if(col){
       return this._["filter"](col,f);
      }
    }.bind(this))();
  }.bind(this));
  
  this.concat = (function(cola, colb){
    return (function(){
      var out = [];
      
      out.push.apply(out, cola);out.push.apply(out, colb);return out;
    
    }.bind(this))();
  }.bind(this));
  
  this.take = (function(n, col){
    return col["slice"](0,n);
  }.bind(this));
  
  this.drop = (function(n, col){
    return (function(){
      
      if(!col) return null;
      
      return col["slice"](n);
    
    }.bind(this))();
  }.bind(this));
  
  this.partition = (function(n, col){
    return (function(){
      var f = (function(out, col){
        return (function(){
          if((0 == this.count(col))){
           return out;
          } else {
           return f(this.concat(out, [
            this.take(n, col)
          ]), this.drop(n, col));
          }
        }.bind(this))();
      }.bind(this));
      
      return f([], col);
    
    }.bind(this))();
  }.bind(this));
  
  this.assoc = (function(obj){
    var rest = Array.prototype.slice.call(arguments, 1);
    return (function(){
      var pairs = this.partition(2, rest);
      
      (function() {
        var G__6227 = pairs;
        for(var i=0; i < G__6227.length; i++) {
          (function(p){(obj[this.first(p)] = this.nth(p, 1))}.bind(this))(G__6227[i]);
        }
      }.bind(this))();return obj;
    
    }.bind(this))();
  }.bind(this));
  
  this.conj = (function(col){
    var rest = Array.prototype.slice.call(arguments, 1);
    (function() {
      var G__6228 = rest;
      for(var i=0; i < G__6228.length; i++) {
        (function(r){col["push"](r)}.bind(this))(G__6228[i]);
      }
    }.bind(this))();
    return col;
  }.bind(this));
  
  this.array_QM_ = (function(o){
    return (o && this._["isArray"](o));
  }.bind(this));
  
  this.object_QM_ = (function(o){
    return (o && (!this.array_QM_(o)) && (!this.string_QM_(o)));
  }.bind(this));
  
  this.string_QM_ = (function(o){
    return this._["isString"](o);
  }.bind(this));
  
  this.element_QM_ = (function(o){
    return (o && (this._["isElement"](o) || this._["isElement"](this.first(o))));
  }.bind(this));
  
  return this.merge = (function(){
    var objs = Array.prototype.slice.call(arguments, 0);
    return (function(){
      var o = ({
        
      });
      
      this.map((function(p1__6226_HASH_){
        return this._["extend"](o,p1__6226_HASH_);
      }.bind(this)), objs);return o;
    
    }.bind(this))();
  }.bind(this))

}).call(cljs.core);



var util = util || {};
(function() {

  this.Array = Array;
  
  for(var prop in cljs.core){ this[prop] = cljs.core[prop] };
  
  this.$ = $;
  
  this.string_QM_ = (function(o){
    return (o && (this.el.constructor == this.String));
  }.bind(this));
  
  this.array_QM_ = (function(el){
    return (el && (el.constructor == this.Array));
  }.bind(this));
  
  this.append = (function(p, c){
    (function(){
      if(this.array_QM_(c)){
       return this.map((function(c){
        return this.append(p, c);
      }.bind(this)), c);
      } else {
       return (function(){p["append"](c);return (function(){
        
        if(!(c instanceof jQuery)) return null;
        
        return c["trigger"]("postinsert");
      
      }.bind(this))()}.bind(this))();
      }
    }.bind(this))();
    return p;
  }.bind(this));
  
  return this.ready = (function(f){
    return this.$(document)["ready"](f);
  }.bind(this))

}).call(util);



var html = html || {};
(function() {

  this.Array = Array;
  
  for(var prop in cljs.core){ this[prop] = cljs.core[prop] };
  
  this.$ = $;
  
  this.util = util;
  
  this.parse_attrs = (function(args){
    return (function(){if((this.nth(args, 1) instanceof jQuery)){return ({
      
    });} else if(this.object_QM_(this.nth(args, 1))){return this.nth(args, 1);} else {return ({
      
    });}}.bind(this))();;
  }.bind(this));
  
  this.parse_body = (function(args){
    return (function(){var out = (function(){if((this.nth(args, 1) instanceof jQuery)){return this.drop(1, args);} else if(this.object_QM_(this.nth(args, 1))){return this.drop(2, args);} else {return this.drop(1, args);}}.bind(this))();;
    out = this.filter(this._.identity, out);out = this.filter((function(p1__8292_HASH_){
      return (!(undefined == p1__8292_HASH_));
    }.bind(this)), out);return out;}.bind(this))();
  }.bind(this));
  
  this.html = (function(args){
    return (function(){if(this.string_QM_(args)){return args;} else if(this.element_QM_(args)){return args;} else if(this.element_QM_(this.first(args))){return args;} else if((args instanceof jQuery)){return args;} else if(this.array_QM_(this.first(args))){return this.map(this.html, args);} else if(true){return (function(){
      var as = this.filter(this._.identity, args),
      tag = this.first(as),
      attrs = this.parse_attrs(as),
      body = this.parse_body(as),
      el = this.$(this.str("<", tag, "/>"));
      
      (function(){
        if(attrs){
         return el["attr"](attrs);
        }
      }.bind(this))();return this.util.append(el, this.map(this.html, body));
    
    }.bind(this))();}}.bind(this))();;
  }.bind(this));
  
  return this.$html = (function(args){
    return this.$(this.html(args));
  }.bind(this))

}).call(html);