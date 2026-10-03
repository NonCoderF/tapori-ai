module.exports = {
  preset: '@react-native/jest-preset',
  setupFilesAfterEnv: ['@testing-library/jest-native/extend-expect'],
  moduleNameMapper: {'^expo-secure-store$': '<rootDir>/jest-secure-store-mock.js'},
  testPathIgnorePatterns: ['/node_modules/', '/android/'],
  transformIgnorePatterns: ['node_modules/(?!((jest-)?react-native|@react-native|@react-navigation))'],
};
