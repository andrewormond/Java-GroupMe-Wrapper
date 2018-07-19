package github.adeo88.groupme.api.events;

public interface GroupEventListener {

	void onFavorite(FavoriteEvent event);

	void onTyping(TypingEvent event);
}
