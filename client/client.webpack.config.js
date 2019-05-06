const merge = require("webpack-merge")

const generatedConfig = require('./scalajs.webpack.config')
const commonClientConfig = require("./scommons.webpack.config.js")
const commonBabelConfig = require("./sc-babel.webpack.config.js")

module.exports = merge(generatedConfig, commonClientConfig, commonBabelConfig)
