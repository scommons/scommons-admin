const merge = require("webpack-merge")

const generatedConfig = require('./scalajs.webpack.config')
const commonConfig = require("./scommons.webpack.config.js")

module.exports = merge(generatedConfig, commonConfig)
