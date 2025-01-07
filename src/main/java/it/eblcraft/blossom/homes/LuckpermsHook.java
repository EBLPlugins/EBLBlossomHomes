package it.eblcraft.blossom.homes;


import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class LuckpermsHook {
    public static int getMaxHomes(ServerPlayerEntity player) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUuid());
        assert user != null;

        QueryOptions queryOptions = QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build();
        Stream<PermissionNode> homeLimitNodes = getHomeLimitNodes(user, queryOptions);

        return homeLimitNodes
                .map(PermissionNode::getPermission)
                .map(permission -> Integer.parseInt(permission.substring("blossom.home.limit.".length())))
                .max(Comparator.comparingInt(Integer::intValue))
                .orElse(0);
    }

    private static Stream<PermissionNode> getHomeLimitNodes(User user, QueryOptions queryOptions) {
        return user.resolveInheritedNodes(queryOptions).stream()
                .filter(node -> node instanceof PermissionNode)
                .map(node -> (PermissionNode) node)
                .filter(node -> node.getPermission().startsWith("blossom.home.limit."));
    }
}
