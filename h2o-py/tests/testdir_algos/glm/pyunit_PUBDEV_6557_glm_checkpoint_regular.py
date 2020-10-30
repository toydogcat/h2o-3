from builtins import range
import sys
sys.path.insert(1,"../../../")
import h2o
from tests import pyunit_utils
from h2o.estimators.glm import H2OGeneralizedLinearEstimator

# check varimp for Binomial, Multinomial, Regression at least.

def testvarimp():
    print("Checking checkpoint for binomials....")
    train = h2o.import_file(path=pyunit_utils.locate("smalldata/glm_test/binomial_20_cols_10KRows.csv"))
    for ind in range(10):
        train[ind] = train[ind].asfactor()
    train["C21"] = train["C21"].asfactor()
    Y = "C21"
    X = list(range(0,20))
    buildModelCheckVarimp(train, X, Y, "binomial")

    print("Checking checkpoint for multinomials....")
    train = h2o.import_file(
        pyunit_utils.locate("smalldata/glm_test/multinomial_10_classes_10_cols_10000_Rows_train.csv"))
    train["C1"] = train["C1"].asfactor()
    train["C2"] = train["C2"].asfactor()
    train["C3"] = train["C3"].asfactor()
    train["C4"] = train["C4"].asfactor()
    train["C5"] = train["C5"].asfactor()
    train["C11"] = train["C11"].asfactor()
    myY = "C11"
    mX = list(range(0,10))
    buildModelCheckVarimp(train, mX, myY, "multinomial")

    print("Checking checkpoint for regression....")
    h2o_data = h2o.import_file(
        path=pyunit_utils.locate("smalldata/glm_test/gaussian_20cols_10000Rows.csv"))
    enum_columns = ["C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10"]
    for cname in enum_columns:
        h2o_data[cname] = h2o_data[cname]
    myY = "C21"
    myX = list(range(20))
    buildModelCheckVarimp(h2o_data, myX, myY, "gaussian")
 

def buildModelCheckVarimp(training_frame, x_indices, y_index, family):
    model = H2OGeneralizedLinearEstimator(family=family, max_iterations=3)
    model.train(training_frame=training_frame, x=x_indices, y=y_index)
    modelCheckpoint = H2OGeneralizedLinearEstimator(family=family, checkpoint=model.model_id)
    modelCheckpoint.train(training_frame=training_frame, x=x_indices, y=y_index)

    modelLong = H2OGeneralizedLinearEstimator(family=family) # allow to run to completion
    modelLong.train(training_frame=training_frame, x=x_indices, y=y_index)

    


if __name__ == "__main__":
  pyunit_utils.standalone_test(testvarimp)
else:
    testvarimp()
