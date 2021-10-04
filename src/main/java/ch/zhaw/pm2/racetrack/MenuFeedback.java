package ch.zhaw.pm2.racetrack;

/**
 * Diese Klasse dient als Container zur Verwaltung der vom User gewählten Menüwerte.
 */
public class MenuFeedback {
    private InputOutput.MenuAction menuAction;
    private PositionVector.Direction direction = PositionVector.Direction.NONE;

    public void setMenuAction(InputOutput.MenuAction menuAction) {
        this.menuAction = menuAction;
    }

    public InputOutput.MenuAction getMenuAction() {
        return menuAction;
    }

    public void setDirection(PositionVector.Direction direction) {
        this.direction = direction;
    }

    public PositionVector.Direction getDirection() {
        return direction;
    }


}
