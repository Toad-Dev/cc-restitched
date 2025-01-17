/*
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */

package dan200.computercraft.api.turtle;

import dan200.computercraft.shared.util.NonNullSupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * A base class for {@link ITurtleUpgrade}s.
 *
 * One does not have to use this, but it does provide a convenient template.
 */
public abstract class AbstractTurtleUpgrade implements ITurtleUpgrade
{
    private final Identifier id;
    private final TurtleUpgradeType type;
    private final String adjective;
    private final NonNullSupplier<ItemStack> stack;

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, String adjective, NonNullSupplier<ItemStack> stack )
    {
        this.id = id;
        this.type = type;
        this.adjective = adjective;
        this.stack = stack;
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, NonNullSupplier<ItemStack> stack )
    {
        this( id, type, Util.createTranslationKey( "upgrade", id ) + ".adjective", stack );
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, String adjective, ItemStack stack )
    {
        this( id, type, adjective, () -> stack );
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, ItemStack stack )
    {
        this( id, type, () -> stack );
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, String adjective, ItemConvertible item )
    {
        this( id, type, adjective, new CachedStack( () -> item ) );
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, ItemConvertible item )
    {
        this( id, type, new CachedStack( () -> item ) );
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, String adjective, Supplier<? extends ItemConvertible> item )
    {
        this( id, type, adjective, new CachedStack( item ) );
    }

    protected AbstractTurtleUpgrade( Identifier id, TurtleUpgradeType type, Supplier<? extends ItemConvertible> item )
    {
        this( id, type, new CachedStack( item ) );
    }

    @Nonnull
    @Override
    public final Identifier getUpgradeID()
    {
        return id;
    }

    @Nonnull
    @Override
    public final String getUnlocalisedAdjective()
    {
        return adjective;
    }

    @Nonnull
    @Override
    public final TurtleUpgradeType getType()
    {
        return type;
    }

    @Nonnull
    @Override
    public final ItemStack getCraftingItem()
    {
        return stack.get();
    }

    /**
     * A supplier which converts an item into an item stack.
     *
     * Constructing item stacks is somewhat expensive due to attaching capabilities. We cache it if given a consistent item.
     */
    private static final class CachedStack implements NonNullSupplier<ItemStack>
    {
        private final Supplier<? extends ItemConvertible> provider;
        private Item item;
        private ItemStack stack;

        CachedStack( Supplier<? extends ItemConvertible> provider )
        {
            this.provider = provider;
        }

        @Nonnull
        @Override
        public ItemStack get()
        {
            Item item = provider.get().asItem();
            if( item == this.item && stack != null ) return stack;
            return stack = new ItemStack( this.item = item );
        }
    }
}
