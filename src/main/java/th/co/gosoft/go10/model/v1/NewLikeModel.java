package th.co.gosoft.go10.model.v1;

import th.co.gosoft.go10.model.v0.TransactionModel;

public class NewLikeModel extends TransactionModel {

    private boolean statusLike;

    public boolean isStatusLike() {
        return statusLike;
    }

    public void setStatusLike(boolean statusLike) {
        this.statusLike = statusLike;
    }
    
}
