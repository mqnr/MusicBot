/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Listener extends ListenerAdapter
{
    private final Bot bot;
    
    public Listener(Bot bot)
    {
        this.bot = bot;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        if(event.getJDA().getGuildCache().isEmpty())
        {
            Logger log = LoggerFactory.getLogger("MusicBot");
            log.warn("This bot is not on any guilds! Use the following link to add the bot to your guilds!");
            log.warn(event.getJDA().getInviteUrl(JMusicBot.RECOMMENDED_PERMS));
        }
        event.getJDA().getGuilds().forEach((guild) -> 
        {
            try
            {
                Settings settings = bot.getSettingsManager().getSettings(guild);
                String defpl = settings.getDefaultPlaylist();
                VoiceChannel vc = settings.getVoiceChannel(guild);
                if(defpl!=null && vc!=null && bot.getPlayerManager().setUpHandler(guild).playFromDefault())
                {
                    guild.getAudioManager().openAudioConnection(vc);
                }
            }
            catch(Exception ignore) {}
        });
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        // TODO: make sure it's in a guild
        bot.getNowplayingHandler().onMessageDelete(event.getGuild(), event.getMessageIdLong());
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        bot.getAloneInVoiceHandler().onVoiceUpdate(event);
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        bot.shutdown();
    }
}
