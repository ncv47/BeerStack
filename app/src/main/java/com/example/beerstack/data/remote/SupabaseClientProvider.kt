package com.example.beerstack.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

//Stored unsafely
//Normally ANON KEY should be in local properties and a link being made from build.gradle.kts like this
/*
buildConfigField(
    "String",
    "SUPABASE_ANON_KEY",
    "\"${project.properties["SUPABASE_ANON_KEY"]}\""
)
 */
//But local properties is different for everyone, so for the convenience of the project for school we leave them in plain text (not secure in a real scenario)
private const val SUPABASE_URL = "https://dqkpzojnslcyeluzzyhi.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRxa3B6b2puc2xjeWVsdXp6eWhpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTYzODgsImV4cCI6MjA4MTAzMjM4OH0.wh3Id22_gXhKNzjIVbyZRUHnU4zhSayxXykxxQNS0WM"

val supabaseClient = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    install(Postgrest)
}