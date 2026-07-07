import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId') || 0))
  const username = ref(localStorage.getItem('username') || '')

  function setUser(t: string, id: number, name: string) {
    token.value = t
    userId.value = id
    username.value = name
    localStorage.setItem('token', t)
    localStorage.setItem('userId', String(id))
    localStorage.setItem('username', name)
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    localStorage.clear()
  }

  return { token, userId, username, setUser, logout }
})
