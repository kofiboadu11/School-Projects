import os
import discord
from riotwatcher import LolWatcher
intents = discord.Intents.default()
intents.message_content = True
from discord.ext import commands

client = discord.Client(intents=intents)
token=''
key=''
watcher=LolWatcher(key)


@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client))

def printStats(summonerName):
    summoner=watcher.summoner.by_name('NA1',summonerName)
    stats=watcher.league.by_summoner('NA1',summoner['id'])
    tier=stats[0]['tier']
    rank=stats[0]['rank']
    lp=stats[0]['leaguePoints']
    wins=int(stats[0]['wins'])
    losses=int(stats[0]['losses'])
    winrate= int((wins/(wins+losses))*100)
    return summonerName + " is currently ranked in "+str(tier),str(rank) +" with "+str(lp)+"LP and a "+str(winrate)+"% winrate."
    
    
    
async def on_ready():
    print('We have logged in as {0.user}'.format(client))
    
@client.event
async def on_message(message):
    if message.author == client.user:
        return
    msg=message.content

    if msg.startswith('$stats'):
        summonerName=msg.split("$stats ",1)[1]
        stat=printStats(summonerName)
          
        await message.channel.send(stat)
client.run(token)
    