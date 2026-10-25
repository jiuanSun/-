module.exports = {
    publicPath: './',
    outputDir: 'dist',
    assetsDir: 'static',
    devServer: {
        host: '0.0.0.0',
        port: 8889,
        disableHostCheck: true,
        proxy: {
            '/api': {
                target: 'http://127.0.0.1:8888',
                changeOrigin: true,
                pathRewrite: { '^/api': '' }
            }
        }
    }
}