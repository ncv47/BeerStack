package com.example.beerstack.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

private const val SUPABASE_URL = "https://dqkpzojnslcyeluzzyhi.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRxa3B6b2puc2xjeWVsdXp6eWhpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTYzODgsImV4cCI6MjA4MTAzMjM4OH0.wh3Id22_gXhKNzjIVbyZRUHnU4zhSayxXykxxQNS0WM"

val supabaseClient = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    install(Postgrest)
}