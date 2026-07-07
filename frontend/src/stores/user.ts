import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId') || 0))
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')

  function setUser(t: string, id: number, name: string, r: string) {
    token.value = t
    userId.value = id
    username.value = name
    role.value = r
    localStorage.setItem('token', t)
    localStorage.setItem('userId', String(id))
    localStorage.setItem('username', name)
    localStorage.setItem('role', r)
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    role.value = ''
    localStorage.clear()
  }

  return { token, userId, username, role, setUser, logout }
})
