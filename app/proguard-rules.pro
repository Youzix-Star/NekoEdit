# miuix and Jetpack Compose are pure Kotlin/Compose code without reflection, so no
# extra keep rules are required for them. The Compose runtime ships its own consumer
# rules through the androidx artifacts.
#
# R8 is what strips the unused Material icons from material-icons-extended; without
# it the release APK would carry several thousand unused ImageVector definitions.
