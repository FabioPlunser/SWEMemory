import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import { resolve } from 'path'

export default defineConfig({
  plugins: [svelte()],
  // root: "./src/",
  // publicDir: './public',
  build: {
    outDir: "../swa/src/main/resources/static/",
    emptyOutDir: true,
    rollupOptions: {
      input: {
        main: resolve(__dirname, "src", "index.html"),
        login: resolve(__dirname, "src", "login", "index.html"),
        register: resolve(__dirname, "src", "register", "index.html"),
        error: resolve(__dirname, "src", "error", "index.html"),
        learn: resolve(__dirname, "src", "learn", "index.html"),
        listCards: resolve(__dirname, "src", "listCards", "index.html"),
        editCards: resolve(__dirname, "src", "editCards", "index.html"),
        createDeck: resolve(__dirname, "src", "createDeck", "index.html"),
        admin: resolve(__dirname, "src", "admin", "index.html"),
        adminShowCards: resolve(__dirname, "src", "admin", "showCards", "index.html"),
        adminShowDecks: resolve(__dirname, "src", "admin", "showDecks", "index.html"),
      }
    }
  }
})
