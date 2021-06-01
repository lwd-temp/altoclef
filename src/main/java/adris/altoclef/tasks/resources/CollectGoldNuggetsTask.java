package adris.altoclef.tasks.resources;

import adris.altoclef.AltoClef;
import adris.altoclef.TaskCatalogue;
import adris.altoclef.tasks.CraftInInventoryTask;
import adris.altoclef.tasks.DefaultGoToDimensionTask;
import adris.altoclef.tasks.MineAndCollectTask;
import adris.altoclef.tasks.ResourceTask;
import adris.altoclef.tasksystem.Task;
import adris.altoclef.util.CraftingRecipe;
import adris.altoclef.util.Dimension;
import adris.altoclef.util.ItemTarget;
import adris.altoclef.util.MiningRequirement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

public class CollectGoldNuggetsTask extends ResourceTask {

    private final int _count;

    public CollectGoldNuggetsTask(int count) {
        super(Items.GOLD_NUGGET, count);
        _count = count;
    }

    @Override
    protected boolean shouldAvoidPickingUp(AltoClef mod) {
        return false;
    }

    @Override
    protected void onResourceStart(AltoClef mod) {

    }

    @Override
    protected Task onResourceTick(AltoClef mod) {
        switch (mod.getCurrentDimension()) {
            case OVERWORLD:
                setDebugState("Getting gold ingots to convert to nuggets");
                int potentialNuggies = mod.getInventoryTracker().getItemCount(Items.GOLD_NUGGET) + mod.getInventoryTracker().getItemCount(Items.GOLD_INGOT) * 9;
                if (potentialNuggies >= _count && mod.getInventoryTracker().hasItem(Items.GOLD_INGOT)) {
                    // Craft gold ingots to nuggets
                    return new CraftInInventoryTask(new ItemTarget(Items.GOLD_NUGGET, _count), CraftingRecipe.newShapedRecipe("golden_nuggets", new ItemTarget[]{new ItemTarget(Items.GOLD_INGOT, 1), null, null, null}, 9));
                }
                // Get gold ingots
                int nuggiesStillNeeded = _count - potentialNuggies;
                return TaskCatalogue.getItemTask("gold_ingot", (int)Math.ceil((double)nuggiesStillNeeded / 9.0));
            case NETHER:
                setDebugState("Mining nuggies");
                return new MineAndCollectTask(Items.GOLD_NUGGET, _count, new Block[]{Blocks.NETHER_GOLD_ORE}, MiningRequirement.WOOD);
            case END:
                setDebugState("Going to overworld");
                return new DefaultGoToDimensionTask(Dimension.OVERWORLD);
        }

        setDebugState("INVALID DIMENSION??: " + mod.getCurrentDimension());
        return null;
    }

    @Override
    protected void onResourceStop(AltoClef mod, Task interruptTask) {

    }

    @Override
    protected boolean isEqualResource(ResourceTask obj) {
        return obj instanceof CollectGoldNuggetsTask;
    }

    @Override
    protected String toDebugStringName() {
        return "Collecting " + _count + " nuggets";
    }
}
