package seedu.lovebook;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.lovebook.commons.core.Config;
import seedu.lovebook.commons.core.LogsCenter;
import seedu.lovebook.commons.core.Version;
import seedu.lovebook.commons.exceptions.DataLoadingException;
import seedu.lovebook.commons.util.ConfigUtil;
import seedu.lovebook.commons.util.StringUtil;
import seedu.lovebook.logic.Logic;
import seedu.lovebook.logic.LogicManager;
import seedu.lovebook.model.DatePrefs;
import seedu.lovebook.model.LoveBook;
import seedu.lovebook.model.Model;
import seedu.lovebook.model.ModelManager;
import seedu.lovebook.model.ReadOnlyDatePrefs;
import seedu.lovebook.model.ReadOnlyLoveBook;
import seedu.lovebook.model.ReadOnlyUserPrefs;
import seedu.lovebook.model.UserPrefs;
import seedu.lovebook.model.util.SampleDataUtil;
import seedu.lovebook.model.util.SampleDatePrefUtil;
import seedu.lovebook.storage.DatePrefsStorage;
import seedu.lovebook.storage.JsonDatePrefsStorage;
import seedu.lovebook.storage.JsonLoveBookStorage;
import seedu.lovebook.storage.JsonUserPrefsStorage;
import seedu.lovebook.storage.LoveBookStorage;
import seedu.lovebook.storage.Storage;
import seedu.lovebook.storage.StorageManager;
import seedu.lovebook.storage.UserPrefsStorage;
import seedu.lovebook.ui.Ui;
import seedu.lovebook.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 2, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing LoveBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        LoveBookStorage loveBookStorage = new JsonLoveBookStorage(userPrefs.getLoveBookFilePath());
        DatePrefsStorage datePrefsStorage = new JsonDatePrefsStorage(userPrefs.getDatePrefsFilePath());
        storage = new StorageManager(loveBookStorage, userPrefsStorage, datePrefsStorage);
        model = initModelManager(storage, userPrefs);
        logic = new LogicManager(model, storage);
        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s LoveBook and {@code userPrefs}. <br>
     * The data from the sample LoveBook will be used instead if {@code storage}'s LoveBook is not found,
     * or an empty LoveBook will be used instead if errors occur when reading {@code storage}'s LoveBook.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getLoveBookFilePath());

        Optional<ReadOnlyLoveBook> loveBookOptional;
        Optional<ReadOnlyDatePrefs> datePrefsOptional;
        ReadOnlyLoveBook initialData;
        ReadOnlyDatePrefs initialDatePrefs;
        try {
            loveBookOptional = storage.readLoveBook();
            if (!loveBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getLoveBookFilePath()
                        + " populated with a sample LoveBook.");
            }
            initialData = loveBookOptional.orElseGet(SampleDataUtil::getSampleLoveBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getLoveBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty LoveBook.");
            initialData = new LoveBook();
        }

        try {
            datePrefsOptional = storage.readDatePrefs();
            if (!datePrefsOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getDatePrefsFilePath()
                        + " populated with a sample DatePrefs.");
            }
            initialDatePrefs = datePrefsOptional.orElseGet(SampleDatePrefUtil::getSamplePreferences);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getDatePrefsFilePath() + " could not be loaded."
                    + " Will be starting with empty preferences.");
            initialDatePrefs = new DatePrefs();
        }

        return new ModelManager(initialData, userPrefs, initialDatePrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting LoveBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }


    @Override
    public void stop() {
        logger.info("============================ [ Stopping LoveBook ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
            storage.saveDatePrefs(model.getDatePrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
