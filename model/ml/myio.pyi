# -*- coding: utf-8 -*-

import pandas as pd
import numpy as np
from typing import Optional, List, Tuple
from typing import overload

@overload
def find_csv_filenames( path_to_dir: str, suffix: Optional[str] ) -> List[str]: ...

# read data
@overload
def read(input_dir: str, max_samples_per_category: int) -> List[pd.DataFrame]: ...

# split into train and test set
@overload
def split_train_test(category_frames: List[pd.DataFrame],  test_size: float) -> Tuple[pd.DataFrame, pd.DataFrame]: ...

@overload
def dataframe_to_numpy_matrix(train_frame: pd.DataFrame, test_frame: pd.DataFrame, mask) -> Tuple[np.matrixlib.defmatrix.matrix,np.matrixlib.defmatrix.matrix]: ...

@overload
def split_target_from_data(all_data: np.matrixlib.defmatrix.matrix) -> Tuple[np.matrixlib.defmatrix.matrix,np.ndarray]: ...



