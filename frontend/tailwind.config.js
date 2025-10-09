/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: '#4C6EF5',
          dark: '#364FC7',
          light: '#EDF2FF'
        }
      },
      boxShadow: {
        soft: '0 10px 30px rgba(76, 110, 245, 0.12)'
      }
    }
  },
  plugins: [
    require('@tailwindcss/forms')
  ]
};
