package com.dslovikosky.narnia.common.event.datagenproviders;

import com.dslovikosky.narnia.common.constants.Constants;
import com.dslovikosky.narnia.common.constants.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModItems.ITEMS.getEntries().forEach(item -> basicItem(item.get()));
    }
}
