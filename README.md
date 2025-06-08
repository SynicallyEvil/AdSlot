# 🎯 AdSlot

**AdSlot** is a Minecraft plugin that lets players purchase in-game advertisement slots through an interactive GUI using the server's economy system. Players can display custom messages and promote themselves or their shops in style!

> 🚧 This plugin is designed for servers looking to give visibility perks to active players, communities, or store owners.

---

## 💡 Features

- 📦 Buyable GUI slots with in-game currency  
- 🧠 Custom slot messages per player  
- 🔐 Slot locking/unlocking support  
- 🗣️ Public announcement toggles  
- 🚫 Player banning/unbanning  
- 💸 Price & duration editable by admins  
- 📅 Configurable rental duration (`default_time`)  
- 🧑‍💼 Admin management commands for full control  

---

## 🧪 Commands

All commands use `/sponsor` (aliases may vary). Permissions are required for administrative actions.

### ✅ Player Commands
- `/sponsor message <message>` – Set your slot's display message.  
- `/sponsor help` – Show help menu.  

### 🛠️ Admin Commands (Requires `adslot.admin`)
- `/sponsor reload` – Reload the config.  
- `/sponsor unlock <ID>` / `/sponsor lock <ID>` – Lock/unlock a slot.  
- `/sponsor announce` – Toggle public announcement broadcasts.  
- `/sponsor edit price <ID> <amount>` – Change price of a slot.  
- `/sponsor edit time <ID> <time>` – Change duration of a slot.  
- `/sponsor set <ID> <player>` – Assign a player to a slot.  
- `/sponsor kick <ID>` → `/sponsor confirm` – Kick player from a slot.  
- `/sponsor editmessage <ID> <message>` – Admin-edit a slot's message.  
- `/sponsor ban <player>` / `/sponsor unban <player>` – Ban or unban a player from using AdSlots.  
- `/sponsor admin` – Show all admin commands.  

---

## 📦 Dependencies

- **Vault** – for economy integration  
- **Spigot 1.13+** – or any compatible fork (e.g., Paper)  

---

## 🛠 Technologies Used

- Java  
- Spigot API  
- Vault API  
- Maven (build system)  

---

## 👤 Author

Made by [SynicallyEvil](https://github.com/SynicallyEvil)  
If this helps your server's economy, leave a ⭐ on GitHub!
