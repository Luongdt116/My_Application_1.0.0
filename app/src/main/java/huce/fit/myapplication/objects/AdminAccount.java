package huce.fit.myapplication.objects;


    private int id;
    private String username;
    private String password;
    private int levelId;

    public AdminAccount(String username, String password, int levelId) {
        this.username = username;
        this.password = password;
        this.levelId = levelId;
    }

}
