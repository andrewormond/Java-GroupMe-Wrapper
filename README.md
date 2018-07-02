# Java Wrapper for the GroupMe V3 API

JavaDoc: https://andrewormond.github.io/Java-GroupMe-Wrapper/

Trello Link: https://trello.com/b/uh7LSr93

*Uses MIT License*


## Examples

##### Create API Object
```java
String token = "your applicationToken"
GroupMeAPI api = new GroupMeAPI(token);
```

##### Get Current Groups
```java
Group[] groups = Group.indexGroups(api);
```

##### Get Specific Group
```java
Group group = Group.show("Group_id", api);
```

##### Simple Echo Bot
Uses the BotManager service to quote all messages it recieves in the group
```java
String botID = "Your Bot ID";
int port = 2000;
BotManager manager = new BotManager(botID, port, api, new BotListener() {
  Bot bot;
  @Override
  public void onMessage(int handlerID, Message message) {
    String response = message.name + " said: \"" + message.text + "\"";
    try {
      bot.postMessage(response, Optional.empty());
    } catch (GroupMeException e) {
      e.printStackTrace();
    }
  }
  @Override
  public void setBot(Bot bot) {
    this.bot = bot;
  }
  @Override
  public Bot getBot() {
    return bot;
  }
});

Thread manThread = new Thread(manager);
manThread.start();
```
