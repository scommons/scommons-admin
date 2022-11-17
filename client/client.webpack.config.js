const { merge } = require("webpack-merge")

const generatedConfig = require('./scalajs.webpack.config')
const commonClientConfig = require("./scommons.webpack.config.js")

module.exports = merge(generatedConfig, commonClientConfig, {
  performance: {
    hints: false
  },

  mode: 'production'
})
