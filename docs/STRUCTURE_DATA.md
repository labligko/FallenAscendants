# Struktur Data dalam Fallen Ascendants

Fallen Ascendants adalah game auto battle TCG berbasis Java LibGDX. Game ini menggunakan beberapa struktur data untuk mengatur kartu, deck, giliran battle, status effect, faction synergy, dan faction counter graph.

## 1. Abstract Data Type (ADT)

ADT digunakan untuk merepresentasikan objek utama dalam game. Setiap objek memiliki atribut dan perilaku masing-masing.

### Card

`Card` merepresentasikan satu kartu karakter dalam game.

Atribut utama:

* ID kartu
* Nama kartu
* Faction
* Role
* Rarity
* HP
* ATK
* DEF
* SPD
* Aggro
* Shield
* Skill aktif
* Passive skill
* Death skill
* Status effect

Fungsi utama:

* Menerima damage
* Menerima heal
* Menambah shield
* Menyimpan status effect
* Mengecek apakah kartu sudah mati

### Skill

`Skill` merepresentasikan kemampuan kartu.

Jenis skill:

* Damage
* Heal
* Shield
* Taunt
* Burn
* Poison
* Slow
* Stun
* Passive
* Death/reactive skill

Skill memiliki trigger, target type, power, cooldown, dan duration.

### BattleField

`BattleField` merepresentasikan area battle pemain atau musuh.

BattleField berisi:

* 5 kartu aktif
* 3 kartu reserve
* Graveyard

Fungsi utama:

* Mengambil kartu aktif
* Mengganti kartu mati dengan kartu reserve
* Mengecek apakah masih ada kartu hidup
* Menyimpan kartu mati ke graveyard

## 2. Array / List

Array atau list digunakan untuk menyimpan kumpulan kartu.

Contoh penggunaan:

* Active field menggunakan slot kartu aktif.
* Reserve menyimpan kartu cadangan.
* Graveyard menyimpan kartu yang sudah kalah.
* Card collection menyimpan kumpulan kartu dalam database.

Pada battle, setiap pemain membawa 8 kartu. Lima kartu pertama masuk ke active field, sedangkan tiga kartu sisanya menjadi reserve.

## 3. Queue

Queue digunakan pada sistem giliran battle.

Game menggunakan `TurnQueue` untuk menentukan urutan kartu yang bergerak berdasarkan SPD. Kartu dengan SPD lebih tinggi mendapat giliran lebih cepat.

Alur kerja:

1. Ambil semua kartu aktif dari player dan enemy.
2. Urutkan berdasarkan SPD.
3. Masukkan ke dalam turn queue.
4. Ambil kartu pertama sebagai attacker.
5. Setelah queue kosong, queue dibangun ulang.

Queue juga membantu menjaga battle tetap otomatis tanpa input manual dari pemain saat pertarungan berlangsung.

## 4. Status Effect System

Status effect digunakan untuk memberi kondisi tertentu pada kartu.

Status yang digunakan:

* Burn
* Poison
* Slow
* Stun

Efek status diproses pada awal giliran kartu.

Contoh:

* Burn memberikan damage tetap.
* Poison memberikan damage berdasarkan persentase max HP.
* Slow menurunkan effective SPD.
* Stun membuat kartu melewati giliran.

## 5. Faction Synergy

Faction synergy adalah sistem bonus tim berdasarkan jumlah faction yang sama di active field.

Synergy hanya menghitung kartu aktif, bukan reserve.

Contoh:

* 2 kartu faction sama: bonus kecil aktif.
* 3 kartu faction sama: bonus lebih kuat.
* 5 kartu faction sama: bonus penuh.

Faction synergy digunakan untuk memberi identitas dan strategi pada deck building.

## 6. Faction Counter Graph

Faction counter menggunakan struktur data graph berarah atau directed graph.

Setiap faction menjadi node, sedangkan hubungan counter menjadi edge.

Graph disimpan menggunakan adjacency list.

Relasi counter:

* Celestial Remnants → Abyssal Church
* Abyssal Church → Mortal Ascendants
* Mortal Ascendants → Dragon Lineage
* Dragon Lineage → Void Corrupted
* Void Corrupted → Shadow Conclave
* Shadow Conclave → Celestial Remnants

Artinya, jika faction attacker memiliki edge menuju faction target, maka counter effect akan aktif.

## 7. Edge-Based Counter Effect

Faction counter tidak hanya memberi bonus damage yang sama untuk semua faction. Setiap edge memiliki efek berbeda agar setiap faction terasa unik.

| Counter                              | Efek                         | Alasan                                          |
| ------------------------------------ | ---------------------------- | ----------------------------------------------- |
| Celestial Remnants → Abyssal Church  | Heal ally dengan HP terendah | Holy/remnant power menekan curse                |
| Abyssal Church → Mortal Ascendants   | Poison target 1 turn         | Manusia mudah dikorupsi                         |
| Mortal Ascendants → Dragon Lineage   | Chance stun 1 turn           | Senjata pemburu naga                            |
| Dragon Lineage → Void Corrupted      | Burn target 1 turn           | Dragon flame membakar void energy               |
| Void Corrupted → Shadow Conclave     | Slow target 1 turn           | Void distortion membongkar stealth dan illusion |
| Shadow Conclave → Celestial Remnants | Damage +20%                  | Assassin menyerang healer atau holy remnant     |

Counter effect aktif jika:

1. Attacker menyerang target dengan faction yang dikalahkan oleh faction attacker.
2. Serangan berupa basic attack.
3. Target menerima HP damage lebih dari 0.

Counter effect tidak aktif pada skill damage area agar balance battle tetap stabil.

## 8. Graph Traversal

Faction Counter Graph juga memiliki traversal menggunakan Depth First Search atau DFS.

DFS digunakan untuk menelusuri node faction dari satu titik awal dan mengikuti edge counter yang tersedia.

Contoh traversal dari `CELESTIAL_REMNANTS`:

1. Visit CELESTIAL_REMNANTS
2. Visit ABYSSAL_CHURCH
3. Visit MORTAL_ASCENDANTS
4. Visit DRAGON_LINEAGE
5. Visit VOID_CORRUPTED
6. Visit SHADOW_CONCLAVE
7. Kembali ke CELESTIAL_REMNANTS karena graph berbentuk cycle

Traversal ini membuktikan bahwa relasi antar faction tidak hanya disimpan sebagai data biasa, tetapi benar-benar dimodelkan sebagai graph.

## 9. Kesimpulan

Struktur data yang digunakan dalam Fallen Ascendants adalah:

| Struktur Data   | Implementasi                                      |
| --------------- | ------------------------------------------------- |
| ADT             | Card, Skill, BattleField, Deck, Player            |
| Array/List      | Active field, reserve, graveyard, card collection |
| Queue           | TurnQueue berdasarkan SPD                         |
| Graph           | Faction Counter Graph                             |
| Graph Traversal | DFS traversal pada faction counter graph          |

Dengan struktur data tersebut, battle system dapat berjalan otomatis, kartu dapat memiliki skill dan status effect, giliran dapat diproses berdasarkan SPD, dan faction dapat memiliki hubungan counter yang strategis.
