# autosniper
- **Basic text based - json loaded autosniper using fabric 1.20.4**

# Commands
- **Commands are via ingame chat**
- **/snipe** -> Enables stats checking (BOOL)
- **/autorq** -> Enables automatic rqing of whatever **/mode** is selected (BOOL)
- **/hud** -> Toggles the text hud for the autosniper (BOOL)
- **/mode** -> Sets the mode you'd like to auto-rq into (String -> 1s, 2s, 3s, 4s, 4v4)
- **/key** -> Sets the API key you're using (String -> Key)
- **/minfkdr** -> Sets the minimum FKDR value you'd like to stay (Float -> 3.5, 70)
- **/target** -> Sets the inputted player's IGN as a "target" & will alert you of their presence (String -> NerdyAss)

# Reqiurements
- Fabric 1.20.4 -> https://www.youtube.com/watch?v=WJcdFPdnj20 fabric tutorial
- Hypixel API Key:
  - Create a hypixel forums account with your main account and/or a gamepass account you **wont** get banned/muted.
  - Acess your API Key here: https://developer.hypixel.net/ (requires **daily** updates for personal keys) - If **everyone** is marked as a nicked player, your key is invalid.
  - You **may** have to wait a day if your forums account is **fresh**

# Notes
- I'm happy to provide **very limited** support for this; if you have an issue **do not** dm/ping me, please make a question thread in discord.gg/nerdyass
- Priority for those that have purchased my configs/Myau
- Forge 1.8.9 only allows for GSON 2.2.x >, which this project has been adapted for, meaning yes, there's some inefficient code, IT'S NOT MY FAULT!! (i am not fixing nerdy's code)

# Planned Additions
- Automatic key validity checks on /key load
- /targets <list> - shows list of current blacklisted targets
- UUID inputs for /target
- Warning messages for rate limits / keys going invalid
- Code checks so invalid keys dont display as a nicked player

# Credits
- Jelteh (Snooc) for the chattriggers
- Elapse for paying for me to bother making this public - visit his shop: gg/elapse (i didn't get paid to make this version public)
- Donate me litecoin cus im broke and making this for free: LRCBu3uoebRcDi2jTZmFNaJPMCyr5ChYvT (nerdy) Ld9X2E9Aq2pT7EXvgxWV35eCuPHATYwyJb (markog)
