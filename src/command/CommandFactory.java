package command;

import com.fasterxml.jackson.databind.JsonNode;
import types.User;

import java.io.IOException;

/**
 * The interface that is implemented by all the commands
 * that the user can execute
 * The commands are implemented in the CommandFactory class
 * and using the getCommand method we return the command
 * we have the Command interface
 */
public class CommandFactory {

    /**
     * @param commandName the name of the command
     * @return the command that the user wants to execute
     */
    public static Command getCommand(final String commandName) {
        switch (commandName) {
            case "search": return new SearchCommand();
            case "load": return new LoadCommand();
            case "status": return new StatusCommand();
            case "playPause": return new PlayPauseCommand();
            case "createPlaylist": return new CreatePlaylistCommand();
            case "addRemoveInPlaylist": return new
                    AddRemoveInPlaylistCommand();
            case "like": return new LikeCommand();
            case "showPlaylists": return new ShowPlaylistsCommand();
            case "showPreferredSongs": return new
                    ShowPreferredSongsCommand();
            case "repeat": return new RepeatCommand();
            case "shuffle": return new ShuffleCommand();
            case "forward": return new ForwardCommand();
            case "backward": return new BackwardCommand();
            case "prev": return new PrevCommand();
            case "next": return new NextCommand();
            case "follow": return new FollowCommand();
            case "switchVisibility": return new SwitchVisibilityCommand();
            case "getTop5Playlists": return new GetTop5PlaylistsCommand();
            case "getTop5Songs": return new GetTop5SongsCommand();
            case "switchConnectionStatus": return new
                    SwitchConnectionStatusCommand();
            case "getOnlineUsers": return new GetOnlineUsersCommand();
            case "addUser": return new AddUserCommand();
            case "addAlbum": return new AddAlbumCommand();
            case "showAlbums": return new ShowAlbumsCommand();
            case "printCurrentPage": return new PrintCurrentPageCommand();
            case "addMerch": return new AddMerchCommand();
            case "addEvent": return new AddEventCommand();
            case "deleteUser": return new DeleteUserCommand();
            case "getAllUsers": return new GetAllUsersCommand();
            case "addPodcast": return new AddPodcastCommand();
            case "addAnnouncement": return new AddAnnouncementCommand();
            case "removeAnnouncement": return new RemoveAnnouncementCommand();
            case "select": return new SelectCommand();
            case "showPodcasts": return new ShowPodcastCommand();
            case "removeAlbum": return new RemoveAlbumCommand();
            case "changePage": return new ChangePageCommand();
            case "removePodcast": return new RemovePodcastCommand();
            case "removeEvent": return new RemoveEventCommand();
            case "getTop5Albums": return new GetTop5AlbumsCommand();
            case "getTop5Artists": return new GetTop5ArtistsCommand();
            case "wrapped": return new WrappedCommand();
            case "buyPremium": return new BuyPremiumCommand();
            case "cancelPremium": return new CancelPremiumCommand();
            case "adBreak": return new AdBreakCommand();
            case "subscribe": return new SubscribeCommand();
            case "getNotifications": return new GetNotificationsCommand();
            case "buyMerch": return new BuyMerchCommand();
            case "seeMerch": return new SeeMerchCommand();
            case "updateRecommendations": return new
                    UpdateRecommendationsCommand();
            case "previousPage": return new PreviousPageCommand();
            case "nextPage": return new NextPageCommand();
            case "loadRecommendations": return new
                    LoadRecommendationsCommand();
            default:
                return null;
        }
    }
}

class SearchCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.searchCommand(user);
    }

 }

class LoadCommand implements Command {
    @Override
    public JsonNode execute(final User user) {
        return CommandRunner.loadCommand(user);
    }

}

class StatusCommand implements Command {
    @Override
    public JsonNode execute(final User user) {
        return CommandRunner.statusCommand(user);
    }
}

class PlayPauseCommand implements Command {
    @Override
    public JsonNode execute(final User user) {
        return CommandRunner.playPauseCommand(user);
    }
}

class CreatePlaylistCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
       return CommandRunner.createPlaylistCommand(user);
    }
}

class AddRemoveInPlaylistCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addRemoveInPlaylistCommand(user);
    }
}
class LikeCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.likeCommand(user);
    }
}

class ShowPlaylistsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.showPlaylistsCommand(user);
    }
}

class ShowPreferredSongsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.showPreferredSongsCommand(user);
    }
}

class RepeatCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.repeatCommand(user);
    }
}

class ShuffleCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.shuffleCommand(user);
    }
}

class ForwardCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.forwardCommand(user);
    }
}

class BackwardCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.backwardCommand(user);
    }

}

class PrevCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.prevCommand(user);
    }
}

class NextCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.nextCommand(user);
    }
}

class FollowCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.followCommand(user);
    }
}

class SwitchVisibilityCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.switchVisibilityCommand(user);
    }
}

class GetTop5PlaylistsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.getTop5PlaylistsCommand();
    }
}

class GetTop5SongsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.getTop5SongsCommand();
    }
}

class SwitchConnectionStatusCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.switchConnectionStatusCommand(user);
    }
}

class GetOnlineUsersCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.getOnlineUsersCommand(user);
    }
}

class AddUserCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addUserCommand(user);
    }
}

class AddAlbumCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addAlbumCommand(user);
    }
}

class ShowAlbumsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.showAlbumsCommand(user);
    }
}

class PrintCurrentPageCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.printCurrentPageCommand(user);
    }
}

class AddMerchCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addMerchCommand(user);
    }
}

class AddEventCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addEventCommand(user);
    }
}

class DeleteUserCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.deleteUserCommand();
    }
}

class GetAllUsersCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.getAllUsersCommand(user);
    }
}

class AddPodcastCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addPodcastCommand(user);
    }
}

class AddAnnouncementCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.addAnnouncementCommand(user);
    }
}

class RemoveAnnouncementCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.removeAnnouncementCommand(user);
    }
}

class SelectCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.selectCommand(user);
    }
}

class ShowPodcastCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.showPodcastsCommand(user);
    }
}

class RemoveAlbumCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.removeAlbumCommand(user);
    }
}

class ChangePageCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.changePageCommand(user);
    }
}

class RemovePodcastCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.removePodcastCommand(user);
    }
}

class RemoveEventCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.removeEventCommand(user);
    }
}

class GetTop5AlbumsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.getTop5AlbumsCommand(user);
    }
}

class GetTop5ArtistsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.getTop5ArtistsCommand(user);
    }
}

class WrappedCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.wrappedCommand(user);
    }
}

class BuyPremiumCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.buyPremiumCommand(user);
    }
}

class  CancelPremiumCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.cancelPremiumCommand(user);
    }
}

class AdBreakCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.adBreakCommand(user);
    }
}

class SubscribeCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.subscribeCommand(user);
    }
}

class GetNotificationsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.notificationCommand(user);
    }
}

class BuyMerchCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.buyMerchCommand(user);
    }
}

class SeeMerchCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.seeMerchCommand(user);
    }
}

class UpdateRecommendationsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.updateRecommandationCommand(user);
    }
}

class PreviousPageCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.previousPageCommand(user);
    }
}

class NextPageCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.nextPageCommand(user);
    }
}

class LoadRecommendationsCommand implements Command {
    @Override
    public JsonNode execute(final User user) throws IOException {
        return CommandRunner.loadRecommendationsCommand(user);
    }
}
