const path = require('path');

const appPath = (...names) => path.join(process.cwd(), ...names);

const {
    createConfig,
    sass,
    entryPoint,
    setOutput,
} = require('webpack-blocks');

//This will be merged with the config from the flavor

module.exports = createConfig([
    entryPoint({
        main: [
            appPath('src', 'index.ts'),
            appPath('src', 'css', 'styles.scss')
        ]
    }),
    setOutput({
        filename: 'bundle.[hash].js',
        path: appPath('build')
    }),
    sass({
        includePaths: [ appPath('node_modules') ],
        sourceMap: true,
    })
]);

// module.exports = {
//     entry: {
//         main: [appPath('src', 'index.ts'), appPath('src', 'css', 'styles.scss')]
//     },
//     output: {
//         filename: 'bundle.[hash].js',
//         path: appPath('build')
//     },
//     module: {
//         rules: [
//             { test: /\.scss$/, use: extractSass.extract([
//                 {
//                     loader: 'sass-loader',
//                     options: {
//                         includePaths: [ appPath('node_modules') ],
//                         sourceMap: true,
//                     },
//                 },
//             ]) }
//         ],
//     },
//     plugins: [
//         extractSass
//     ],
// };
