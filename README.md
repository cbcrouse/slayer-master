# Slayer Master

A plugin for assisting players with slayer tasks.

## Feature Roadmap

* Type of slayer items required for defend against monster (e.g., nose peg)
* Type of weapon/item required to kill (e.g, leaf bladed weapons)
* Recommended location(s) of monster (2 or 3)
* Recommended items to bring (bone crusher/herb sack, etc), can check in bank and only recommend if have the item.
* Preferred Gear/Attack Style (e.g., Best Melee/Best Prayer), could show alternative strategies and preferred
* User preference based skip option. User able to mark a task as skip or do for example
* Recommended skip/no skip and reasons
* Show average gold per kill based on wiki data (try not to use static info)
* Show if superior and how to fight them
* Show fastest route, could integrate with shortest path plugin or just do quest helper like directions
* Potentially a panel of unlocks. This could be recommended slayer items such as bone crusher as well as point unlocks. Use Emote Clue Items for reference or example of how to display them.
* Could activate automatically when getting a new task, but should still be usable off-task to get the same help. Similar to quest helper's helpers.
* Show a panel of monsters to choose from, similar to quest helper. Should show red if cannot do and tooltip for why. Should show like orange if difficult and tooltip for why. Should show green if doable. Show grey if out-leveled or task too easy based on combat level, slayer xp? Panel should have search feature to filter the list.
* Show monster variants (e.g., spectres/wolves/etc), show if boss can be done
* Show potential for nightmare zone (not sure on this one)

## 

Dependencies

Adding `id 'com.github.ben-manes.versions' version '0.51.0'` makes it possible to run the gradle command `dependencyUpdates`.

How to see which dependencies need to be updated:

```cmd
./gradlew dependencyUpdates
```

When updating the dependencies, navigate to the `build.gradle` file and update the dependency versions.
