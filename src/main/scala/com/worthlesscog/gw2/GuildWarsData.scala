package com.worthlesscog.gw2

import scala.collection.immutable.SortedMap

object GuildWarsData {

    val dyeSets = SortedMap(
        "Awakened" -> Set("Embalm", "Sarcophagus", "Scourge", "Tar", "Urn", "Vabbian Bronze"),
        "Bloodstone" -> Set("Bloodstone Coral", "Bloodstone Dark Coral", "Bloodstone Dark Indigo", "Bloodstone Dark Violet", "Bloodstone Indigo", "Bloodstone Violet"),
        "Blue Shift" -> Set("Baby Blue", "Blue Steel", "Ghost", "Lapis", "Phthalo Blue", "Powder Blue", "Resolution", "Valor", "Zaffre"),
        "Crimson Lion" -> Set("Aureus", "Crimson Lion", "Golden Lion", "Imperial Gold", "Imperial Red", "Rosewood"),
        "Deathly" -> Set("Acid", "Acrid", "Algae", "Caustic", "Swampblack", "Toxin"),
        "Elonian Beasts" -> Set("Choya", "Desert Harpy", "Hydra", "Iboga", "Jacaranda", "Sand Shark"),
        "Elonian Landscape" -> Set("Highlands", "Mesa", "Oasis", "Ruin", "Sandstorm", "Sulfur"),
        "Flame" -> Set("Charred", "Cinders", "Flame", "Flare", "Molten", "Pyre"),
        "Frost" -> Set("Deep Glacial Sky", "Deep Glacial Teal", "Glacial Sky", "Glacial Teal", "Shiver Sea", "Shiver Sky"),
        "Glint's Winter" -> Set("Glint's Ambition", "Glint's Crystal", "Glint's Isolation", "Glint's Purview", "Glint's Rebellion", "Glint's Sanctuary"),
        "Jormag" -> Set("Core Ice", "Eternal Ice", "Frostbite Blue", "Frostbite Green", "Frozen Scales", "Permafrost"),
        "Kralkatorrik" -> Set("Arcane", "Branded", "Brandstorm", "Crystal", "Fear", "Shattered"),
        "Lion's Arch Commemorative" -> Set("Enameled Anamnesis", "Enameled Generation", "Enameled Longevity", "Enameled Perseverance", "Enameled Sacrifice", "Enameled Solitude"),
        "Lion's Arch Rebuild" -> Set("Enameled Amenity", "Enameled Banana", "Enameled Brass", "Enameled Morning Glory", "Enameled Onset", "Enameled Strawberry"),
        "Lion's Arch Survivors" -> Set("Enameled Crimson", "Enameled Emblaze", "Enameled Jungle", "Enameled Legacy", "Enameled Reign", "Enameled Sky"),
        "Mad King" -> Set("Carnage Orange", "Crushed Bone", "Eerie Purple", "Ember Red", "Harrowing Maroon Dye", "Ominous Yellow"),
        "Metallurgic" -> Set("Amenity", "Fling", "Onset", "Perseverance", "Prosperity", "Recall"),
        "Mordremoth" -> Set("Abyssal Forest", "Auric", "Arid", "Bloom", "Jungle", "Nightmare"),
        "Primordus" -> Set("Bloody Red", "Destroyer Orange", "Incandescent", "Magma", "Scorched", "Sunfire Lava"),
        "Sacred" -> Set("Radiant Gold", "Cerulean Sky", "Dawn", "Cerulean Night", "Radiant Brass", "Celestial Blue"),
        "Shadow" -> Set("Shadow Abyss", "Shadow Blue", "Shadow Green", "Shadow Magenta", "Shadow Orange", "Shadow Purple", "Shadow Red", "Shadow Turquoise", "Shadow Violet", "Shadow Yellow"),
        "Solar and Lunar" -> Set("Blue Whale", "Daybreak", "Glossy Black", "Midnight Bronze", "Rose Gold", "Silver Satin"),
        "Taimi" -> Set("Electro Blue", "Electro Lemon", "Electro Lime", "Electro Peach", "Electro Pink", "Electro Purple"),
        "Toxic" -> Set("Blacklight", "Cobolt", "Cyanide", "Limonite", "Vincent", "Violite"),
        "Vibrant" -> Set("Aqua Satin", "Blue Orchid", "Magenta", "Pumpkin Orange", "Ruby Red", "Slime Green"),
        "Winter Chimes" -> Set("Dry Silver", "Gold Fusion", "Golden Sheen", "Matriarch Brass", "Murky Grey", "Vintage Silver"),
        "Zhaitan" -> Set("Abyssal Sea", "Decay", "Risen", "Grave", "Darkness", "Worn Bone"))

    // XXX - messy
    val mountPacks = SortedMap(
        "Default" -> Set(
            "Raptor/raptor",
            "Springer/springer",
            "Skimmer/skimmer",
            "Jackal/jackal",
            "Griffon/griffon",
            "Roller Beetle/roller_beetle",
            "Skyscale/skyscale"),
        "Awakened Mounts" -> Set(
            "Awakened Raptor/raptor",
            "Awakened Springer/springer",
            "Awakened Skimmer/skimmer",
            "Awakened Jackal/jackal",
            "Awakened Griffon/griffon"),
        "Branded Mounts" -> Set(
            "Branded Raptor/raptor",
            "Branded Springer/springer",
            "Branded Skimmer/skimmer",
            "Branded Jackal/jackal",
            "Branded Griffon/griffon"),
        "Cozy Wintersday Mounts" -> Set(
            "Cozy Wintersday Raptor/raptor",
            "Cozy Wintersday Springer/springer",
            "Cozy Wintersday Skimmer/skimmer",
            "Cozy Wintersday Jackal/jackal",
            "Cozy Wintersday Griffon/griffon"),
        "Desert Racer" -> Set(
            "Ntouka Snakescale/raptor", "Starscale/raptor",
            "Kourna Jackrabbit/springer", "Painted Mesa/springer",
            "Arid Hammerhead/skimmer", "Iceberg/skimmer",
            "Dune Mastiff/jackal", "Mirror Masked/jackal",
            "Northern Feather Wing/griffon", "Storm Chaser/griffon", "Tufted Night Eye/griffon",
            "Colossal Ladybird/roller_beetle", "Funerary Scarab/roller_beetle", "Primeval/roller_beetle", "Trailblazer/roller_beetle"),
        "Distant Lands" -> Set(
            "Auric Salamander/raptor", "Luminous Prowler/raptor",
            "Krytan Lop/springer", "Starfall/springer", "Timberland Badged/springer",
            "Banded Wave Fin/skimmer", "Southsun Torpedo/skimmer",
            "Exalted Ley Hunter/jackal", "Liminal Moorhound/jackal",
            "Sand Souled/griffon", "Shiverpeaks Pileated/griffon", "Snow Stalker/griffon",
            "Alloyed Construct/roller_beetle", "Jagged Kournan/roller_beetle", "Striped Bahdzan/roller_beetle"),
        "Exo-Suit Mounts" -> Set(
            "Exo-Suit Raptor/raptor",
            "Exo-Suit Springer/springer",
            "Exo-Suit Skimmer/skimmer",
            "Exo-Suit Jackal/jackal",
            "Exo-Suit Griffon/griffon"),
        "Exotic Breeds" -> Set(
            "Masked Runner/raptor", "Primordial Raptor/raptor", "Exalted Gate Guardian/raptor",
            "Savannah Stripetail/springer", "Brisban Brushtail/springer", "Alloyed Irontail/springer",
            "Spotted Orchid/skimmer", "Sand Souled/skimmer",
            "Vabbian Lightpaw/jackal", "Alloyed Quicksilver/jackal",
            "Kournan Bright-Tail/griffon", "Primeval/griffon", "Arctic Icewing/griffon",
            "Ntouka Snakeshell/roller_beetle", "Starshell/roller_beetle"),
        "Gem Store" -> Set(
            "Dreadnought/raptor", "Gallant Lightbearer/raptor", "Resplendent Avialan/raptor",
            "Summit Wildhorn/springer", "Sun Temple Gecko/springer",
            "Magnificent Hummingbird/skimmer", "Umbral Demon/skimmer",
            "Reforged Warhound/jackal", "Shrine Guardian/jackal",
            "Grand Lion/griffon", "Nightfang/griffon",
            "Tremor Armadillo/roller_beetle",
            "Shimmerwing/skyscale"),
        "Istani Isles" -> Set(
            "Dzalana Ice Scale/raptor", "Lahtenda Bog Hunter/raptor", "Striped Jarin/raptor",
            "Issnur Long Hair/springer", "Primeval/springer", "Sargol Thunderer/springer",
            "Oceanic Broadtail/skimmer", "Primus Beta/skimmer", "Stardrift/skimmer",
            "Ceylon Cut/jackal", "Iceflow/jackal", "Primeval/jackal",
            "Exalted Sky Sentry/griffon", "Istani Bald/griffon", "Sky Bandit/griffon"),
        "Mad Realm Mounts" -> Set(
            "Mad Realm Raptor/raptor",
            "Mad Realm Springer/springer",
            "Mad Realm Skimmer/skimmer",
            "Mad Realm Jackal/jackal",
            "Mad Realm Griffon/griffon"),
        "Mount Adoption" -> Set(
            "Canyon Spiketail/raptor", "Coastal Spiketail/raptor", "Flamelander/raptor", "Savannah Monitor/raptor", "Storm Ridge/raptor", "Striped Tri-horn/raptor",
            "Arctic Jerboa/springer", "Desert Lop/springer", "Elonian Jackalope/springer", "Primal Hare/springer", "Tawny Hare/springer", "Windy Spot/springer",
            "Bright Ringfin/skimmer", "Dajkah Lantern/skimmer", "Oasis Skate/skimmer", "Oceanic Ray/skimmer", "River Moth Wing/skimmer", "Spined Longtail/skimmer",
            "Banded Mystic/jackal", "Crowned Ancient/jackal", "Polished Stone/jackal", "Pyroclast/jackal", "Stardust/jackal", "Twin Sands/jackal",
            "Badlands Stalker/griffon", "Clouded Corvus/griffon", "Fire Pinion/griffon", "Highlands Harrier/griffon", "Spotted Sylph/griffon", "Starbound/griffon"),
        "New Horizons" -> Set(
            "Lithosol/raptor", "Vital Geode/raptor",
            "Cascade Heeler/springer", "Maguma Jackrabbit/springer",
            "Luminous Ray/skimmer",
            "Fulgurite Ridgeback/jackal", "Sun Stalker/jackal",
            "Crowned Skyfisher/griffon",
            "Arctic Razor/roller_beetle", "Deep Desert Scarab/roller_beetle",
            "Savage Gladiator/warclaw", "Starclaw/warclaw",
            "Broad-Horn Bull/skyscale", "Glademire/skyscale", "Incarnate Flame/skyscale"),
        "Shiverpeaks Mounts" -> Set(
            "Shiverpeaks Raptor/raptor",
            "Shiverpeaks Springer/springer",
            "Shiverpeaks Skimmer/skimmer",
            "Shiverpeaks Jackal/jackal",
            "Shiverpeaks Griffon/griffon"),
        "Skyscale Ascension" -> Set(
            "Caldera Skyscale/skyscale",
            "Axejaw Skyscale/skyscale",
            "Flare-Marked Skyscale/skyscale",
            "Bioluminescent Skyscale/skyscale",
            "Branded Skyscale/skyscale"),
        "Spooky Mounts" -> Set(
            "Spooky Raptor/raptor",
            "Spooky Springer/springer",
            "Spooky Skimmer/skimmer",
            "Spooky Jackal/jackal",
            "Spooky Griffon/griffon"),
        "Warclaw Frontline" -> Set(
            "Assault Warclaw/warclaw",
            "Branded Warclaw/warclaw",
            "Embermane Warclaw/warclaw",
            "Outrider Warclaw/warclaw",
            "Vanguard Warclaw/warclaw")
    )

}
