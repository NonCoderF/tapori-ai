const values = new Map();
module.exports = {
  getItemAsync: async key => values.get(key) ?? null,
  setItemAsync: async (key, value) => { values.set(key, value); },
  deleteItemAsync: async key => { values.delete(key); },
};
