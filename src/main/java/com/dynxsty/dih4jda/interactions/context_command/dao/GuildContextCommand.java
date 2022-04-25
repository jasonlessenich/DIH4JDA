package com.dynxsty.dih4jda.interactions.context_command.dao;


import java.util.List;

public abstract class GuildContextCommand extends BaseContextCommand {
	private List<Long> includedGuilds;

	private List<Long> excludedGuilds;

	public List<Long> getIncludedGuilds() {
		return includedGuilds;
	}

	public void setIncludedGuilds(List<Long> includedGuilds) {
		this.includedGuilds = includedGuilds;
	}

	public List<Long> getExcludedGuilds() {
		return excludedGuilds;
	}

	public void setExcludedGuilds(List<Long> excludedGuilds) {
		this.excludedGuilds = excludedGuilds;
	}
}
