# Copyright 2024-2025 Oğuzhan Topaloğlu
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.





# ##########################################################
# ################    NOTICE CHECKER v2    #################
# ##########################################################
#
# This script performs checks for various project integrity related things.
# 
# These things include: 
#     - Checks for license notices in your source files
#     - Checks for copyright notices in your source files
#     - Checks for any unnecessary empty files (files with 0 bytes)
# 
# Important Notes:
#     - This script is designed to be mainly used by project managers but it can be used by anyone.
#     - This script only does checks and warns the user, IT DOES NOT CHANGE ANY FILE CONTENTS.
#     - This script was written in Python 3.9.0, but it will probably work in all >=3 versions.
#     - You must customize the variables in the "CUSTOMIZABLE VARIABLES" section for this
#         script to work properly
#     - This script has no dependencies, meaning you only need core Python modules to run it.
#         There is no need for any virtual environments (venv's) or PIP installations.
# 


#######################  CUSTOMIZABLE VARIABLES  #######################


# This path specifies your project's root path, either write an absolute path
#   or simply write "./" if this script is already in your project's root
PROJECT_ROOT_PATH = r'./'

# The copyright owner's name that will be checked in the copyright notices
COPYRIGHT_OWNER_NAME = "Oğuzhan Topaloğlu"

# The project's license, only "GPLv3" and "Apache-2.0" are allowed for now
PROJECT_LICENSE = "Apache-2.0"

# Files with the following relative paths will be skipped by this script.
# These paths must be written with "/" separator for the script to be cross-platform 
SKIP_FILES = [
    "LICENSE", "LICENSE.txt", "README.md", ".gitignore", "AUTHORS", "AUTHORS.txt",
    "notice_checker.bat", "gradlew.bat", "gradlew", "settings.gradle",
    "build.gradle"
]

# Files with the following extensions will be skipped by this script
SKIP_FILE_EXTENSIONS = [
    ".png", ".jpg", ".jpeg", ".mp3", ".wav", ".mp4", ".exe"
]
 
# Directories with the following paths (relative to the PROJECT_ROOT_PATH) will be skipped
SKIP_DIRECTORIES = [
    "build", "dist", "legal", "resources", "res", ".git",
    "gradle", ".gradle", ".idea", "test-assets"
]

# Defines how many lines will be read from a file while checking for copyright notices.
# You can set this variable to -1 if you want to read the whole file.
# Keep in mind that some files might contain lines before their copyright notices, so
#   you should probably not choose a small integer like 10. (example: "#!/bin/sh")
FIRST_N_LINES_FOR_COPYRIGHT_NOTICE = 20


# Defines how many lines will be read from a file while checking for license notices.
# You can set this variable to -1 if you want to read the whole file.
FIRST_N_LINES_FOR_LICENSE_NOTICE = 30


# This string will be printed in the console if there are no errors in your whole repository
SUCCESS_MESSAGE = "SUCCESS, EVERYTHING CHECKS OUT!!!"


#######################  MAIN FUNCTIONS  #######################


import datetime
import sys
import os
import re


# Recursively traverses the project, starting from PROJECT_ROOT_PATH.
# It determines and returns the files that will be checked by the script.
# It filters everything by SKIP_FILES, SKIP_FILE_EXTENSIONS, SKIP_DIRECTORIES.
def get_to_be_checked_files() -> list[str]:
    file_list = []
    
    for root, dirs, files in os.walk(PROJECT_ROOT_PATH):
        if any(skip_dir in root for skip_dir in SKIP_DIRECTORIES):
            continue

        for file in files:
            extension = os.path.splitext(file)[1]
            full_fpath = os.path.join(root, file)
            relative_fpath = os.path.relpath(full_fpath, PROJECT_ROOT_PATH).replace(os.path.sep, '/')
            
            if (relative_fpath in SKIP_FILES) or (extension in SKIP_FILE_EXTENSIONS):
                continue

            file_list.append(full_fpath)
    
    return file_list



# Checks and reports empty files (files with 0 byte size) in the repository
def check_empty_files(files: list[str]) -> bool:
    empty_files = [file for file in files if os.path.getsize(file) == 0]
    log_if_exists("Your repository contains the following empty files:", empty_files)
    
    return len(empty_files) == 0



# Checks and reports if there are any files without a license notice
def check_license_notices(files: list[str]) -> bool:
    wrong_files = []

    for file_path in files:
        file_content = read_file_content(file_path, first_n_lines=FIRST_N_LINES_FOR_LICENSE_NOTICE, as_string=True) 

        # Check for different substrings of the project's license's notice in the file_content.
        # Note: since the line-breaks of the file might be different on projects, this script
        #   only checks for small substrings instead of the whole lines of the notice or the 
        #   whole contents of the notice
        if PROJECT_LICENSE == "Apache-2.0":
            b1 = "Licensed under the Apache License, Version 2.0" in file_content
            b2 = "you may not use this file except in compliance" in file_content
            b3 = "You may obtain a copy of the License at" in file_content
            b4 = "http://www.apache.org/licenses/LICENSE-2.0" in file_content
            b5 = "Unless required by applicable law or agreed to" in file_content
            b6 = "distributed under the License is distributed" in file_content
            b7 = "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND" in file_content
            b8 = "See the License for the specific language governing" in file_content
            b9 = "limitations under the License." in file_content
            notice_is_okay = b1 and b2 and b3 and b4 and b5 and b6 and b7 and b8 and b9
            if not notice_is_okay:
                wrong_files.append(file_path)
            
        elif PROJECT_LICENSE == "GPLv3":
            b1 = "is free software: you can redistribute it" in file_content
            b2 = "the terms of the GNU General Public License" in file_content
            b3 = "as published by the Free Software" in file_content
            b4 = "Foundation, either version 3 of the License" in file_content
            b5 = "is distributed in the hope that it will be useful" in file_content
            b6 = "without even the implied warranty of" in file_content
            b7 = "MERCHANTABILITY or FITNESS FOR A" in file_content
            b8 = "PARTICULAR PURPOSE." in file_content
            b9 = "See the GNU General Public License" in file_content
            b10 = "You should have received a copy of the GNU General" in file_content
            b11 = "If not, see <https://www.gnu.org/licenses/>." in file_content
            notice_is_okay = b1 and b2 and b3 and b4 and b5 and b6 and b7 and b8 and b9 and b10 and b11
            if not notice_is_okay:
                wrong_files.append(file_path)
        else:
            raise ValueError(f"Invalid PROJECT_LICENSE variable: '{PROJECT_LICENSE}'. "
                  "Please fix your invalid variable and re-run the script.")

    log_if_exists(
        "The following files DO NOT contain license notices:", wrong_files
    )

    return len(wrong_files) == 0
    


# Checks and reports if there are any files without a valid copyright notice
def check_copyright_notices(files: list[str]) -> bool:
    no_error_occured = True
    errfiles_no_cp_notice = []
    
    for file in files:
        first_lines = read_file_content(file, first_n_lines=FIRST_N_LINES_FOR_COPYRIGHT_NOTICE, as_string=False)
        detected_cp_lines = [line.rstrip('\n') for line in first_lines if "Copyright" in line]
        
        # No line containing the word "Copyright" means there is no cp notice in the file
        if len(detected_cp_lines) == 0:
            no_error_occured = False
            errfiles_no_cp_notice.append(file)
            continue

        potential_fixes = []
        has_valid_notice = False

        # Now we will go over the all the cp lines to see if any of them are valid
        for cp_line in detected_cp_lines:
            valid_line, error_reason = is_valid_cpline(cp_line)
            if not valid_line:
                potential_fixes.append("\t["+ error_reason + "] "+ "{" + cp_line + "}")
            else:
                has_valid_notice = True
                break
        
        if has_valid_notice:
            continue
        
        # If the code reaches here, it means there was no valid cp notice so we need to log
        no_error_occured = False
        print(f"No valid copyright notice was found for:\nFile path: {file}\nPotential fixes:")
        for e in potential_fixes: print(e)
        print()
    
    log_if_exists(
        "The following files DO NOT have a copyright notice:", errfiles_no_cp_notice
    )

    return no_error_occured



#######################  HELPER FUNCTIONS  #######################


def is_valid_cpline(cp_line: str) -> tuple[bool, str]:
    CURRENT_YEAR = str(datetime.date.today().year)

    # Line must contain the current year, ex "2023"
    if not(CURRENT_YEAR in cp_line):
        return False, "MISSING YEAR"
    
    # Line must contain a logical order of years when multiple years are provided
    year_interval_match = re.search(r'(\d{4})-(\d{4})', cp_line)
    if year_interval_match:
        year_left, year_right = year_interval_match.groups()
        if year_left == CURRENT_YEAR: # "2023-<any year in future>"
            return False, "COPYRIGHTING THE FUTURE"
        if year_left >= year_right: # "2023-2023", "2023-2022" etc.
            return False, "INVALID YEAR INTERVAL"

    # Line must contain the copyright owner's name
    if re.search(r'\b' + re.escape(COPYRIGHT_OWNER_NAME) + r'\b', cp_line) is None:
        return False, "MISSING NAME"

    # Line must match this format:  Copyright <optional (C)> <"cur year" or "year1-cur year"> <any chars>
    if re.search(r"Copyright(?: \(C\))? \d{4}(?:-\d{4})? .+", cp_line) is None:
        return False, "INVALID FORMAT"

    # Line must not contain any unnecessary spaces, ex "Copyright 2023 John Doe,  Steve Doe"
    if "  " in cp_line.strip():
        return False, "UNNECESSARY SPACES"
    
    # cp_line="Copyright 2023 John Doe, Steve Doe"  =>  authors_string="John Doe, Steve Doe"
    authors_string = cp_line[cp_line.index(CURRENT_YEAR)+len(CURRENT_YEAR):].strip()

    # Authors string must not contain any illegal characters: punctuations except , - ( )
    for illegal_char in r"""!"#$%&'*+.-/:;<=>?@[\]^_`{|}~""":
        if illegal_char in authors_string:
            return False, "ILLEGAL CHAR"
    
    # Authors string must start with a name, not a ','
    if authors_string[0] == ',':
        return False, "STARTS WITH COMMA"

    # A list containing full names, for example ["John Doe", "Steve Doe"]
    fullnames_list = [x.strip() for x in authors_string.split(",")]

    # Check if there are any duplicate names in the fullnames list
    if len(fullnames_list) != len(set(fullnames_list)):
        return False, "DUPLICATE NAMES"

    # Check for double commas between names "John Doe,, Steve Doe" which is a mistake
    if '' in fullnames_list:
        return False, "REPEATED COMMA"

    # All of these fullnames must be in title case
    for fullname in fullnames_list:
        names = fullname.split(" ")
        for name in names:
            if name[0].islower():
                return False, "NON-TITLE CASE NAME"
        
    return True, "No error occured"



def log_if_exists(message: str, _list: list[str]) -> None:
    if len(_list) > 0:
        print("\n" + message)
        for term in _list: 
            print("\t", term)
        print()



def read_file_content(file_path: str, first_n_lines: int = -1, as_string: bool = True):
    try:
        if first_n_lines == -1: # read the whole file
            with open(file_path, "r", encoding="utf-8") as f:
                whole_file = f.read()
                if as_string:
                    return whole_file
                else:
                    return whole_file.split('\n')
        else:
            with open(file_path, "r", encoding="utf-8") as file:
                result = []
                for unused in range(first_n_lines):
                    line = file.readline()
                    if line.strip() != "" and line != "\n":
                        result.append(line)

                if as_string:
                    return ''.join(result)
                else:
                    return result
    except:
        sys.exit(f"An error occured while trying to read this file: {file_path}")




#######################  MAIN  #######################


if __name__ == '__main__':
    files = get_to_be_checked_files()
    
    success1 = check_empty_files(files)
    success2 = check_license_notices(files)
    success3 = check_copyright_notices(files)

    if success1 and success2 and success3:
        print(SUCCESS_MESSAGE)
    


