package fr.velocity.init;

import fr.velocity.video.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Tabs extends CreativeTabs {

    public static CreativeTabs VP = new Tabs("videoplayer_tab", new ItemStack(Item.getItemFromBlock(ModBlocks.TV_BLOCK)), false);

    private final ItemStack icon;
    private final String label;
    private final boolean hasSearchBar;

    public Tabs(String label, Item icon, boolean hasSearchBar) {
        this(label, new ItemStack(icon), hasSearchBar);
    }

    public Tabs(String label, ItemStack icon, boolean hasSearchBar) {
        super(label);

        this.label = label;
        this.icon = icon;
        this.hasSearchBar = hasSearchBar;
    }

    @Override
    public String getTabLabel() {
        return this.label;
    }

    @Override
    public ItemStack createIcon() {
        return this.icon;
    }

    @Override
    public boolean hasSearchBar() {
        return this.hasSearchBar;
    }
}