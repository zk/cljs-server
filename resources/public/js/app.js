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
    }.bind(this)), "", this.filter((function(p1__5250_HASH_){
      return this._["identity"](p1__5250_HASH_);
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
        var G__5252 = pairs;
        for(var i=0; i < G__5252.length; i++) {
          (function(p){(obj[this.first(p)] = this.nth(p, 1))}.bind(this))(G__5252[i]);
        }
      }.bind(this))();return obj;
    
    }.bind(this))();
  }.bind(this));
  
  this.conj = (function(col){
    var rest = Array.prototype.slice.call(arguments, 1);
    (function() {
      var G__5253 = rest;
      for(var i=0; i < G__5253.length; i++) {
        (function(r){col["push"](r)}.bind(this))(G__5253[i]);
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
      
      this.map((function(p1__5251_HASH_){
        return this._["extend"](o,p1__5251_HASH_);
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
    out = this.filter(this._.identity, out);out = this.filter((function(p1__6908_HASH_){
      return (!(undefined == p1__6908_HASH_));
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



var widgets = widgets || {};
(function() {

  this.Array = Array;
  
  for(var prop in cljs.core){ this[prop] = cljs.core[prop] };
  
  for(var prop in html){ this[prop] = html[prop] };
  
  this.$ = $;
  
  this._ = _;
  
  this.defaults = ({
    
  });;
  
  this.tab = (function(title, content){
    return (function(){
      var link = this.$html([
        "a",
        ({
          'href':"#"
        }),
        title
      ]);
      
      return ({
        'title':link,
        'content':content,
        'focus':(function(){
          return (function(){link["addClass"]("selected");return content["css"](({
            'display':"block"
          }))}.bind(this))();
        }.bind(this)),
        'blur':(function(){
          return (function(){link["removeClass"]("selected");return content["css"](({
            'display':"none"
          }))}.bind(this))();
        }.bind(this)),
        'click':(function(p1__6909_HASH_){
          return link["click"](p1__6909_HASH_);
        }.bind(this))
      });
    
    }.bind(this))();
  }.bind(this));
  
  this.tab_panel = (function(){
    var tabs = Array.prototype.slice.call(arguments, 0);
    return (function(){
      var el = this.$html([
        "div",
        ({
          'class':"tab-panel"
        }),
        [
          "div",
          ({
            'class':"tabs"
          }),
          this.map((function(p1__6910_HASH_){
            return (p1__6910_HASH_['title']);
          }.bind(this)), tabs),
          [
            "div",
            ({
              'class':"clear"
            })
          ]
        ],
        [
          "div",
          ({
            'class':"clear"
          })
        ],
        [
          "div",
          ({
            'class':"tab-content"
          }),
          this.map((function(p1__6911_HASH_){
            return (p1__6911_HASH_['content']);
          }.bind(this)), tabs)
        ]
      ]);
      
      (function() {
        var G__6913 = tabs;
        for(var i=0; i < G__6913.length; i++) {
          (function(t){t["click"]((function(){
            this.map((function(p1__6912_HASH_){
              return p1__6912_HASH_["blur"]();
            }.bind(this)), tabs);
            t["focus"]();
            return false;
          }.bind(this)));
          t["blur"]()}.bind(this))(G__6913[i]);
        }
      }.bind(this))();this.first(tabs)["focus"]();return el;
    
    }.bind(this))();
  }.bind(this));
  
  this.panel = (function(){
    return this.$html([
      "div",
      ({
        'class':"panel"
      })
    ]);
  }.bind(this));
  
  this.h_splitter = (function(){
    return this.$html([
      "div",
      ({
        'class':"h-splitter"
      })
    ]);
  }.bind(this));
  
  this.size_h_split_pane = (function(container, left_el, left_opts, right_el){
    return (function(){
      var w = container["width"](),
      h = container["height"](),
      left_width = ((left_opts['width']) || 200);
      
      left_el["css"](({
        'height':h,
        'width':left_width
      }));return right_el["css"](({
        'height':h,
        'width':(function() {
          var out = arguments[0];
          for(var __i=1; __i<arguments.length; __i++) {
            out = out - arguments[__i];
          }
          return out;
        }).call(this, w, left_width)
      }));
    
    }.bind(this))();
  }.bind(this));
  
  this.h_split_pane = (function(left, right){
    return (function(){
      var container = (function(){var out = this.$html([
        "div",
        ({
          'class':"h-split-pane"
        })
      ]);
      out["css"](({
        'width':"100%",
        'height':"100%"
      }));
      return out}.bind(this)()),
      left_el = this.$html([
        "div",
        ({
          'class':"left-pane",
          'style':"float: left; width: 100%; height: 100%;"
        }),
        (left['el'])["css"](({
          'width':"100%",
          'height':"100%"
        }))
      ]),
      right_el = this.$html([
        "div",
        ({
          'class':"right-pane",
          'style':"float: left;"
        }),
        (right['el'])["css"](({
          'width':"100%",
          'height':"100%"
        }))
      ]);
      
      container["empty"]();this.util.append(container, left_el);this.util.append(container, right_el);this.util.append(container, this.$html([
        "div",
        ({
          'style':"clear: both"
        })
      ]));container["resize"]((function(){
        return this.size_h_split_pane(container, left_el, left, right_el);
      }.bind(this)));this.on_insert(container, (function(){
        return this.size_v_split_pane(container, this.top_el, this.bottom_el, this.top);
      }.bind(this)));return container;
    
    }.bind(this))();
  }.bind(this));
  
  this.size_v_split_pane = (function(container, top_el, bottom_el){
    return (function(){
      
      if(!(container && top_el && bottom_el)) return null;
      
      return (function(){
        var w = container["outerWidth"](),
        h = container["outerHeight"](),
        top_height = ((function() {
          var out = arguments[0];
          for(var __i=1; __i<arguments.length; __i++) {
            out = out + arguments[__i];
          }
          return out;
        }).call(this, top_el["height"]()) || 100),
        bottom_height = (function() {
          var out = arguments[0];
          for(var __i=1; __i<arguments.length; __i++) {
            out = out - arguments[__i];
          }
          return out;
        }).call(this, h, top_height);
        
        top_el["css"](({
          'height':top_height
        }));return bottom_el["css"](({
          'height':bottom_height
        }));
      
      }.bind(this))();
    
    }.bind(this))();
  }.bind(this));
  
  this.on_insert = (function(el, f){
    return el["bind"]("DOMNodeInserted",(function(){
      return (function(){
        
        if(!(el["parents"]("body")[0])) return null;
        
        return f();
      
      }.bind(this))();
    }.bind(this)));
  }.bind(this));
  
  return this.v_split_pane = (function(top, bottom){
    return (function(){
      var container = (function(){var out = this.$html([
        "div",
        ({
          'class':"v-split-pane"
        })
      ]);
      out["css"](({
        'width':"100%",
        'height':"100%"
      }));
      return out}.bind(this)()),
      top_el = this.$html([
        "div",
        ({
          'class':"top-pane"
        }),
        (top['el'])
      ]),
      bottom_el = this.$html([
        "div",
        ({
          'class':"bottom-pane"
        }),
        (bottom['el'])
      ]);
      
      container["empty"]();this.util.append(container, top_el);this.h_splitter();this.util.append(container, bottom_el);container["resize"]((function(){
        return this.size_v_split_pane(container, top_el, bottom_el, top);
      }.bind(this)));this.on_insert(container, (function(){
        return this.size_v_split_pane(container, top_el, bottom_el, top);
      }.bind(this)));return container;
    
    }.bind(this))();
  }.bind(this))

}).call(widgets);



var app = app || {};
(function() {

  this.Array = Array;
  
  for(var prop in cljs.core){ this[prop] = cljs.core[prop] };
  
  for(var prop in util){ this[prop] = util[prop] };
  
  for(var prop in html){ this[prop] = html[prop] };
  
  this.wd = widgets;
  
  this.CodeMirror = CodeMirror;
  
  this.MirrorFrame = MirrorFrame;
  
  this.control_panel = (function(){
    return this.$html([
      "div",
      ({
        'class':"hello world"
      }),
      [
        "h1",
        "Control Panel"
      ]
    ]);
  }.bind(this));
  
  return this.ready((function(){
    return (function(){
      var header = this.$html([
        "header",
        [
          "h1",
          "CljsPad"
        ],
        [
          "div",
          ({
            'id':"content"
          })
        ]
      ]),
      content = this.$html([
        "div",
        ({
          'style':"background-color: green; height: 100%;"
        }),
        this.wd.h_split_pane(({
          'el':this.control_panel()
        }), ({
          'el':this.$html([
            "div",
            ({
              'class':"cljs-editor-wrapper"
            }),
            [
              "textarea",
              ({
                'id':"cljs-editor",
                'style':""
              }),
              "(ns cljspad)"
            ]
          ])
        }))
      ]);
      
      this.$("body")["css"](({
        'backgroundColor':"orange"
      }));this.util.append(this.$("body"), this.wd.v_split_pane(({
        'el':header
      }), ({
        'el':content
      })));return this.CodeMirror["fromTextArea"]("cljs-editor",({
        'path':"/js/codemirror/",
        'content':this.$("#cljs-editor")["val"](),
        'height':"100%",
        'width':"100%",
        'parserfile':[
          "parsescheme.js",
          "tokenizescheme.js"
        ],
        'stylesheet':"/css/schemecolors.css"
      }));
    
    }.bind(this))();
  }.bind(this)))

}).call(app);