//package com.deadshotmdf.SkyblockCollector.Commands;
//
//import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
//import com.deadshotmdf.SkyblockCollector.Objects.Collector;
//import com.deadshotmdf.SkyblockCollector.SC;
//import com.deadshotmdf.SkyblockCollector.Utils.Utils;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.World;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.scheduler.BukkitRunnable;
//
//public class StressTest implements CommandExecutor {
//
//    private SC main;
//    private CollectorManager collectorManager;
//
//    public StressTest(SC main, CollectorManager collectorManager) {
//        this.main = main;
//        this.collectorManager = collectorManager;
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        if(!sender.getName().equalsIgnoreCase("DeadshotMDF")){
//            sender.sendMessage("Command only for testing, msg me remove it");
//            return true;
//        }
//        sender.sendMessage("Start");
//
//        if(args.length == 0) {
//            Location loc = ((Player) sender).getLocation();
//            World w = loc.getWorld();
//            ItemStack item = new ItemStack(Material.ROTTEN_FLESH);
//            stress2(w, loc, item);
//            return true;
//        }
//
//        int count = 0;
//        int limit = Utils.getInteger(args[0]);
//        World w = ((Player)sender).getWorld();
//        for(int x = 0; x > -limit; x -= 18)
//            for(int z = 0; z >= -10000; z -= 18)
//                System.out.println(x + " " +  z + " " + ++count + " " + collectorManager.placeCollector(new Collector(((Player) sender).getUniqueId(), new Location(w, x, 0, z))));
//
//        sender.sendMessage("Done");
//        return true;
//    }
//
//    private void stress2(World w, Location loc, ItemStack stack){
//        new BukkitRunnable() {
//            int stop = 0;
//            @Override
//            public void run() {
//                if(++stop > 200)
//                    this.cancel();
//
//                for(int i = 0; i < 100; i++)
//                    w.dropItem(loc, stack);
//            }
//        }.runTaskTimer(main, 1, 1);
//    }
//}
