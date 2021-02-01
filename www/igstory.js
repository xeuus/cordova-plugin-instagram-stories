
var exec = require('cordova/exec');

var PLUGIN_NAME = 'IGStory';

var IGStory = {
  shareToStory: function(opts, cb, err) {
    exec(cb, err, PLUGIN_NAME, 'shareToStory', [opts.backgroundImage, opts.attributionURL, opts.imageUrl]);
  }
};

module.exports = IGStory;
