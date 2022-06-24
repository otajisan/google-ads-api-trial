const defaultTheme = require('tailwindcss/defaultTheme');

module.exports = {
    mode: 'jit',
    purge: ['./src/pages/**/*.{js,ts,jsx,tsx}', './src/components/**/*.{js,ts,jsx,tsx}'],
    darkMode: false,
    theme: {
        fontFamily: {
            ja: [...defaultTheme.fontFamily.sans],
            en: [...defaultTheme.fontFamily.sans],
        },
        extend: {
            colors: {
                theme: {
                    light: '#ffffff',
                    medium: '#cccccc',
                    DEFAULT: '#242424',
                    dark: '#111111',
                },
                primary: {
                    DEFAULT: '#242424',
                },
            },
        },
    },

    variants: {
        extend: {},
    },
    plugins: [],
};
