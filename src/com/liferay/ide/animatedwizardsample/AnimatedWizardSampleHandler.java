package com.liferay.ide.animatedwizardsample;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.oomph.util.UIUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AnimatedWizardSampleHandler extends AbstractHandler
{
    public AnimatedWizardSampleHandler()
    {
    }

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final Shell parentShell = Display.getCurrent().getActiveShell();

        UIUtil.syncExec( parentShell, new Runnable()
        {
            public void run()
            {
                GearShell shell = new GearShell( parentShell );
                shell.open();
            }
        } );

        return null;
    }
}
