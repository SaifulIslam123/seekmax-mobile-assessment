import {Handler} from 'express';
import userSchema from '../../models/UserSchema';
import crypto from 'crypto';
import jwt from 'jsonwebtoken';
import {config} from '../../config';
import applicationSchema from "../../models/ApplicationSchema";

export const authHandler: Handler = async (req, res) => {
    const {user: username, password} = req.body;

    try {
        const user = await userSchema.findOne({username});

        if (!user) {
            return res.status(401).send('Either username or password are wrong!');
        }

        const [salt, key] = user.password.split(':');

        const hashedInput = await crypto.scryptSync(password, salt, 64);

        if (hashedInput.toString('hex') !== key) {
            return res.status(401).send('Either username or password are wrong!');
        }

        const token = jwt.sign(
            {userId: user._id, display: user.displayname},
            config.jwtSecret,
            {expiresIn: '8h'},
        );

        return res.status(200).json(token);
    } catch (err) {
        return res.status(500).send();
    }
};

export const getUserById: Handler = async (req, res) => {
    const {id} = req.params;

    if (id !== req.userId) {
        return res.status(403).send();
    }

    try {
        const user = await userSchema.findOne({_id: id});

        return res.status(200).json(user);
    } catch (err) {
        return res.status(500).send();
    }
};

export const updateUserNameHandler: Handler = async (req, res) => {
    if (req.userId === 'unauthenticated') {
        return res.status(403).send('UnAuthorized!');
    }
    const {name} = req.body;

    const result = await userSchema.updateOne({_id: req.userId}, {$set: {username: name}});
    return res.status(201).send(result.acknowledged);
};


export const updatePasswordHandler: Handler = async (req, res) => {
    if (req.userId === 'unauthenticated') {
        return res.status(403).send('UnAuthorized!');
    }
    const {password} = req.body;

    const salt = crypto.randomBytes(16).toString('hex');
    const encrypted = await crypto.scryptSync(password, salt, 64);
    const pwd = `${salt}:${encrypted.toString('hex')}`;

    const result = await userSchema.updateOne({_id: req.userId}, {$set:{password:pwd}});

    return res.status(201).send(result.acknowledged);

};